// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.patterns

import com.barrybecker4.simulation.waveFunctionCollapse.model.ByteArray

import java.awt.Color
import java.awt.image.BufferedImage

/** Color indexing helpers for overlapping patterns. Does not retain the seed bitmap. */
class PatternColorSamples private (private val samples: PatternSamples, private val N: Int) {

  private val numColors: Int = samples.colors.size
  private val w: Long = Math.pow(numColors.toDouble, (N * N).toDouble).toLong

  def getPatternSymmetries(x: Int, y: Int, symmetry: Int = 8): Array[ByteArray] =
    samples.getPatternSymmetries(x, y, symmetry)

  def colors: Seq[Color] = samples.colors

  def index(p: ByteArray): Long = {
    var result: Long = 0
    var power: Long = 1
    for (i <- p.indices) {
      result += p(p.length - 1 - i) * power
      power *= numColors
    }
    result
  }

  def patternFromIndex(ind: Long): ByteArray = {
    var residue: Long = ind
    var power: Long = w
    val result: ByteArray = new ByteArray(N * N)

    for (i <- result.indices) {
      power /= numColors
      var count = 0
      while (residue >= power) {
        residue -= power
        count += 1
      }
      result(i) = count.toByte
    }
    result
  }
}

object PatternColorSamples {
  def apply(bitmap: BufferedImage, N: Int): PatternColorSamples =
    new PatternColorSamples(PatternSamples(bitmap, N), N)
}
