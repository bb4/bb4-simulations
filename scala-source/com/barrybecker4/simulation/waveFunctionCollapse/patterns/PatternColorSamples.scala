// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.patterns

import com.barrybecker4.simulation.waveFunctionCollapse.model.ByteArray

import java.awt.Color
import java.awt.image.BufferedImage

case class PatternColorSamples(bitmap: BufferedImage, N: Int) {

  private val samples: PatternSamples = PatternSamples(bitmap, N)

  private val numColors: Int = samples.colors.size
  private val w: Long = Math.pow(numColors.toDouble, (N * N).toDouble).toLong

  def  getPatternSymmetries(x: Int, y: Int): Array[ByteArray] = samples.getPatternSymmetries(x, y)

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
      //assert (count < 256 && count >= 0, s"count = $count")
      result(i) = count.toByte
    }
    //assert(result.forall(x => x >= 0), "One of the values was negative in " +  result.mkString(", ") + s"\n indices=${result.indices.mkString(", ")}")
    result
  }
}
