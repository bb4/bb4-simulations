/* Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.utils.Utils

import java.awt.image.BufferedImage
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


abstract class Model(name: String, val FMX: Int, val FMY: Int) {

  type IntArray = Array[Int]
  type DoubleArray = Array[Double]
  var dxFun: IntArray = Array[Int](-1, 0, 1, 0)
  var dyFun: IntArray = Array[Int](0, 1, 0, -1)
  private val opposite = Array[Int](2, 3, 0, 1)
  var wave: Array[Array[Boolean]] = _
  var propagator: Array[Array[IntArray]] = _
  private var compatible: Array[Array[IntArray]] = _
  var observed: IntArray = _
  private var stack: Array[(Int, Int)] = _
  private var random: Random = _
  var weights: DoubleArray = _
  private var weightLogWeights: DoubleArray = _
  private var sumsOfOnes: IntArray = _
  private var sumsOfWeights: DoubleArray = _
  private var sumsOfWeightLogWeights: DoubleArray = _
  private var entropies: DoubleArray = _
  private var sumOfWeights: Double = 0
  private var sumOfWeightLogWeights: Double = 0
  private var startingEntropy: Double = 0
  protected var tCounter: Int = 0
  private var stackSize: Int = 0
  var periodic: Boolean = false

  def getName: String = name

  private def init(): Unit = {

    wave = Array.fill(FMX * FMY)(null)
    compatible = Array.fill(FMX * FMY)(null)

    for (i <- wave.indices) {
      wave(i) = Array.fill(tCounter)(false)
      compatible(i) = Array.fill(tCounter)(null)

      for (t <- 0 until tCounter) {
        if (compatible(i) != null) {
          compatible(i)(t) = Array.fill(4)(0)
        }
      }
    }

    weightLogWeights = Array.ofDim[Double](tCounter)
    sumOfWeights = 0.0
    sumOfWeightLogWeights = 0.0

    for (t <- 0 until tCounter) {
      weightLogWeights(t) = weights(t) * Math.log(weights(t))
      sumOfWeights += weights(t)
      sumOfWeightLogWeights += weightLogWeights(t)
    }

    startingEntropy = Math.log(sumOfWeights) - sumOfWeightLogWeights / sumOfWeights

    sumsOfOnes = Array.fill(FMX * FMY)(0)
    sumsOfWeights = Array.fill(FMX * FMY)(0)
    sumsOfWeightLogWeights = Array.fill(FMX * FMY)(0)
    entropies = Array.fill(FMX * FMY)(0)

    stack = Array.fill(wave.length * tCounter)(null)
    stackSize = 0
  }


  private def observe(): Option[Boolean] = {
    var min = 1E+3
    var argMin = -1

    for (i <- wave.indices) {
      if (!onBoundary(i % FMX, i / FMX)) {
        val amount = sumsOfOnes(i)
        if (amount == 0) return Some(false)

        val entropy = entropies(i)
        if (amount > 1 && entropy <= min) {
          val noise = 1E-6 * random.nextDouble()
          if (entropy + noise < min) {
            min = entropy + noise
            argMin = i
          }
        }
      }
    }

    if (argMin == -1) {
      observed = Array.fill(FMX * FMY)(0)
      for (i <- wave.indices) {
        breakable {
          for (t <- 0 until tCounter) {
            if (wave(i) != null && wave(i)(t)) {
              assert(observed != null)
              observed(i) = t
              break
            }
          }
        }
      }
      return Some(true)
    }

    val distribution = Array.fill(tCounter)(0.0)
    for (t <- 0 until tCounter) {
      distribution(t) = if (wave(argMin) != null && wave(argMin)(t)) weights(t) else 0.0
    }
    val r = Utils.randomFromArray(distribution, random.nextDouble())

    val w = wave(argMin)
    for (t <- 0 until tCounter)
      if (w != null && w(t) != (t == r)) ban(argMin, t)

    None
  }

  def onBoundary(x: Int, y: Int): Boolean

  def ban(i: Int, t: Int): Unit = {
    if (wave(i) != null)
        wave(i)(t) = false

    if (compatible(i) != null) {
      val comp = compatible(i)(t)
      for (d <- 0 to 3) comp(d) = 0
    }

    stack(stackSize) = (i, t)
    stackSize += 1

    var sum = sumsOfWeights(i)
    entropies(i) += sumsOfWeightLogWeights(i) / sum - Math.log(sum)

    sumsOfOnes(i) -= 1
    sumsOfWeights(i) -= weights(t)
    sumsOfWeightLogWeights(i) -= weightLogWeights(t)

    sum = sumsOfWeights(i)
    entropies(i) -= sumsOfWeightLogWeights(i) / sum - Math.log(sum)
  }

  protected def propagate(): Unit = {
    while (stackSize > 0) {
      val e1 = stack(stackSize - 1)
      stackSize -= 1

      val i1 = e1._1
      val x1 = i1 % FMX
      val y1 = i1 / FMX

      for (d <- 0 to 3) {
        val dx = dxFun(d)
        val dy = dyFun(d)
        var x2 = x1 + dx
        var y2 = y1 + dy
        if (!onBoundary(x2, y2)) {
          if (x2 < 0) x2 += FMX
          else if (x2 >= FMX) x2 -= FMX
          if (y2 < 0) y2 += FMY
          else if (y2 >= FMY) y2 -= FMY

          val i2 = x2 + y2 * FMX
          val p = propagator(d)(e1._2) // check for null  propagator(d)?
          val compat = compatible(i2)

          for (l <- 0 until (if (p == null) 0 else p.length)) {
            val t2 = if (p == null) 0 else p(l)
            val comp = if (compat == null) null else compat(t2)

            if (comp != null) {
              comp(d) -= 1
              if (comp(d) == 0) ban(i2, t2)
            }
          }
        }
      }
    }
  }

  def run(seed: Int, limit: Int): Boolean = {
    if (wave == null) init()

    clear()
    random = new Random(seed)

    var l = 0
    do {
      val result = observe()
      if (result.isDefined) {
        return result.get
      }
      propagate()
      l += 1
    } while (l < limit || limit == 0)

    true
  }

  def clear(): Unit = {
    for (i <- wave.indices) {
      for (t <- 0 until tCounter) {
        if (wave(i) != null) {
          wave(i)(t) = true
          for (d <- 0 to 3)
            //propagator(opposite(d))?.get(t)?.size?.let { compatible(i)?.get(t)?.set(d, it) }
            compatible(i)(t)(d) = propagator(opposite(d))(t).length // not sure
        }
      }

      sumsOfOnes(i) = weights.length
      sumsOfWeights(i) = sumOfWeights
      sumsOfWeightLogWeights(i) = sumOfWeightLogWeights
      entropies(i) = startingEntropy
    }
  }

  def graphics(): BufferedImage
}
