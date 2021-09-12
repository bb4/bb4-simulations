// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors.OverlappingImageExtractor
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.Overlapping
import com.barrybecker4.simulation.waveFunctionCollapse.model.propagators.OverlappingPropagator
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.readImage

import java.awt.{Color, Dimension}
import java.awt.image.BufferedImage
import scala.collection.mutable
import scala.util.control.Breaks.{break, breakable}


class OverlappingModel(val name: String,
  val N: Int, width: Int, height: Int, periodicInput: Boolean,
  periodicOutput: Boolean, symmetry: Int, groundParam: Int, val limit: Int = 0
) extends WfcModel(name, width, height, limit) {

  private var patterns: Array[ByteArray] = _
  private var colors: Seq[Color] = Seq()
  private var ground: Int = 0

  periodic = periodicOutput
  val bitmap: BufferedImage = readImage(s"samples/$name.png")

  val smx: Int = bitmap.getWidth
  val smy: Int = bitmap.getHeight
  val sample: Array[ByteArray] = Array.ofDim[Byte](smx, smy)
  dimensions = new Dimension(width, height)

  val start1 = System.currentTimeMillis()
  for (y <- 0 until smy) {
    for (x <- 0 until smx) {
      val color = new Color(bitmap.getRGB(x, y))
      var i = 0
      breakable {
        for (c <- colors) {
          if (c == color)
            break()
          i += 1
        }
      }

      if (i == colors.length) colors :+= color
      sample(x)(y) = i.toByte
    }
  }
  println("done calc sample in " + (System.currentTimeMillis() - start1) / 1000.0)

  val c: Int = colors.size
  val w: Long = Math.pow(c.toDouble, (N * N).toDouble).toLong

  def this(overlapping: Overlapping) = {
    this(
      overlapping.getName,
      overlapping.getN,
      overlapping.getWidth,
      overlapping.getHeight,
      overlapping.getPeriodicInput,
      overlapping.getPeriodic,
      overlapping.getSymmetry,
      overlapping.getGround,
      overlapping.getLimit)
  }

  def pattern(passedInFunc: (Int, Int) => Byte): ByteArray = {
    val result: ByteArray = new Array(N * N)
    for (y <- 0 until N)
      for (x <- 0 until N)
        result(x + y * N) = passedInFunc(x, y)
    result
  }

  def patternFromSample(x: Int, y: Int): ByteArray = {
    pattern((dx, dy) => sample((x + dx) % smx)((y + dy) % smy))
  }

  def rotate(p: ByteArray): ByteArray = {
    pattern((x, y) => p(N - 1 - y + x * N))
  }

  def reflect(p: ByteArray): ByteArray = {
     pattern( (x, y) => p(N - 1 - x + y * N))
  }

  def index(p: ByteArray): Long = {
    var result: Long = 0
    var power: Long = 1
    for (i <- p.indices) {
      result += p(p.length - 1 - i) * power
      power *= c
    }
    result
  }

  def patternFromIndex(ind: Long): ByteArray = {
    var residue = ind
    var power: Long = w
    val result: ByteArray = new ByteArray(N * N)

    for (i <- result.indices) {
      power /= c
      var count = 0

      while (residue >= power) {
        residue -= power
        count += 1
      }
      result(i) = count.toByte
    }
    result
  }

  val weightsMap: mutable.Map[Long, Int] = new mutable.HashMap[Long, Int]()
  var ordering: Seq[Long] = Seq()

  println("sym = " + symmetry)
  val start = System.currentTimeMillis()
  for (y <- 0 until (if (periodicInput) smy else smy - N + 1)) {
    for (x <- 0 until (if (periodicInput) smx else smx - N + 1)) {
      val ps: Array[ByteArray] = Array.fill(8)(null)
      ps(0) = patternFromSample(x, y)
      ps(1) = reflect(ps(0))
      ps(2) = rotate(ps(0))
      ps(3) = reflect(ps(2))
      ps(4) = rotate(ps(2))
      ps(5) = reflect(ps(4))
      ps(6) = rotate(ps(4))
      ps(7) = reflect(ps(6))

      for (k <- 0 until symmetry) {
        val ind: Long = index(ps(k))
        if (weightsMap.contains(ind)) {
          weightsMap(ind) += 1
        } else {
          weightsMap(ind) = 1
          ordering :+= ind
        }
      }
    }
  }
  println("done calc sym in " + (System.currentTimeMillis() - start) / 1000.0)

  tCounter = weightsMap.size
  ground = (groundParam + tCounter) % tCounter
  patterns = Array.fill(tCounter)(null)
  this.weights = Array.fill(tCounter)(0)

  var counter = 0
  for (orderItem <- ordering) {
    patterns(counter) = patternFromIndex(orderItem)
    this.weights(counter) = weightsMap.getOrElse(orderItem, 0).toDouble
    counter += 1
  }

  propagator = new OverlappingPropagator(tCounter, patterns, N)

  override def onBoundary(x: Int, y: Int): Boolean = {
    !periodic && (x + N > FMX || y + N > FMY || x < 0 || y < 0)
  }

  override def clear(): Unit = {
    super.clear()

    if (ground != 0) {
      for (x <- 0 until FMX) {
        for (t <- 0 until tCounter)
          if (t != ground) wave.ban(x + (FMY - 1) * FMX, t, weights)
        for (y <- 0 until FMY - 1)
          wave.ban(x + y * FMX, ground, weights)
      }

      wave.propagate(onBoundary, weights, propagator)
    }
  }

  def graphics(): BufferedImage  = {
    assert(this.isReady)
    val imageExtractor = new OverlappingImageExtractor(dimensions, N, tCounter, patterns, colors, onBoundary)
    imageExtractor.getImage(wave)
  }

  override def toString: String = {
    // val N: Int, width: Int, height: Int, periodicInput: Boolean,
    // periodicOutput: Boolean, symmetry: Int, groundParam: Int, val limit: Int = 0
    s"OverlappingModel($getName N=$N periodicOutput=$periodicOutput periodicInput=$periodicInput symmetry=$symmetry ground=$groundParam limit=$limit)"
  }
}
