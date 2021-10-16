// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.patterns

import com.barrybecker4.simulation.waveFunctionCollapse.model.{ByteArray, DoubleArray, OverlappingImageParams}

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable


/**
  * Used by OverlappingModel to extract patters form image
  */
case class PatternExtractor(bitmap: BufferedImage, imageParams: OverlappingImageParams) {

  var patterns: Array[ByteArray] = _
  var weights: DoubleArray = _
  var ground: Int = 0
  var tCounter: Int = 0

  private val smx: Int = bitmap.getWidth
  private val smy: Int = bitmap.getHeight

  private val patternColorSamples = PatternColorSamples(bitmap, imageParams.N)
  private val N = imageParams.N

  initialize()

  def colors: Seq[Color] = patternColorSamples.colors

  private def initialize(): Unit = {
    val weightsMap: mutable.Map[Long, Int] = new mutable.HashMap[Long, Int]()
    var ordering: Seq[Long] = Seq()

    println("sym = " + imageParams.symmetry)
    val start = System.currentTimeMillis()
    val periodicInput = imageParams.periodicInput
    for (y <- 0 until (if (periodicInput) smy else smy - N + 1)) {
      for (x <- 0 until (if (periodicInput) smx else smx - N + 1)) {
        val patternSymmetries = patternColorSamples.getPatternSymmetries(x, y)

        for (k <- 0 until imageParams.symmetry) {
          val ind: Long = patternColorSamples.index(patternSymmetries(k))
          if (weightsMap.contains(ind)) {
            weightsMap(ind) += 1
          } else {
            weightsMap(ind) = 1
            ordering :+= ind
          }
        }
      }
    }
    println("done calc symmetry in " + (System.currentTimeMillis() - start) / 1000.0)

    tCounter = weightsMap.size
    ground = (imageParams.groundParam + tCounter) % tCounter
    patterns = Array.fill(tCounter)(null)
    this.weights = Array.fill(tCounter)(0)

    var counter = 0
    for (orderItem <- ordering) {
      val pattern = patternColorSamples.patternFromIndex(orderItem)
      this.patterns(counter) = pattern
      this.weights(counter) = weightsMap.getOrElse(orderItem, 0).toDouble
      counter += 1
    }
  }

}
