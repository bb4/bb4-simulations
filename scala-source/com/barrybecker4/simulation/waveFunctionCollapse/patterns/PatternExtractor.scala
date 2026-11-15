// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.patterns

import com.barrybecker4.simulation.waveFunctionCollapse.model.{ByteArray, DoubleArray, OverlappingImageParams}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.WfcDebug

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable


/**
  * Used by OverlappingModel to extract patterns from image.
  * Symmetry variants are counted as separate patterns (matches mxgmn/WFC).
  * The seed [[BufferedImage]] is not retained after construction.
  */
class PatternExtractor private (
  val tCounter: Int,
  val ground: Int,
  val patterns: Array[ByteArray],
  val weights: DoubleArray,
  private val patternColors: Seq[Color]
) {
  def colors: Seq[Color] = patternColors
}

object PatternExtractor {

  def apply(bitmap: BufferedImage, imageParams: OverlappingImageParams): PatternExtractor = {
    val smx = bitmap.getWidth
    val smy = bitmap.getHeight
    val patternColorSamples = PatternColorSamples(bitmap, imageParams.N)
    val N = imageParams.N

    val (weightsMap, ordering) = collectPatternWeights(patternColorSamples, imageParams, smx, smy, N)
    val tCounter = weightsMap.size
    val ground = (imageParams.groundParam + tCounter) % tCounter
    val patterns: Array[ByteArray] = Array.ofDim(tCounter)
    val weights: DoubleArray = Array.ofDim(tCounter)

    var counter = 0
    for (orderItem <- ordering) {
      patterns(counter) = patternColorSamples.patternFromIndex(orderItem)
      weights(counter) = weightsMap.getOrElse(orderItem, 0).toDouble
      counter += 1
    }

    new PatternExtractor(tCounter, ground, patterns, weights, patternColorSamples.colors)
  }

  /** Scan the sample image and count each pattern index (including symmetry variants). */
  private def collectPatternWeights(
      patternColorSamples: PatternColorSamples,
      imageParams: OverlappingImageParams,
      smx: Int,
      smy: Int,
      N: Int): (mutable.Map[Long, Int], mutable.ArrayBuffer[Long]) = {
    val weightsMap: mutable.Map[Long, Int] = new mutable.HashMap[Long, Int]()
    val ordering = mutable.ArrayBuffer[Long]()

    if (WfcDebug.enabled) println("sym = " + imageParams.symmetry)
    val start = System.currentTimeMillis()
    val periodicInput = imageParams.periodicInput
    val symmetry = imageParams.symmetry
    for (y <- 0 until (if (periodicInput) smy else smy - N + 1)) {
      for (x <- 0 until (if (periodicInput) smx else smx - N + 1)) {
        val patternSymmetries = patternColorSamples.getPatternSymmetries(x, y, symmetry)
        for (k <- 0 until symmetry) {
          val ind: Long = patternColorSamples.index(patternSymmetries(k))
          if (weightsMap.contains(ind)) weightsMap(ind) += 1
          else {
            weightsMap(ind) = 1
            ordering += ind
          }
        }
      }
    }
    if (WfcDebug.enabled)
      println("done calc symmetry in " + (System.currentTimeMillis() - start) / 1000.0)
    (weightsMap, ordering)
  }
}
