// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.patterns

import com.barrybecker4.simulation.waveFunctionCollapse.model.ByteArray
import com.barrybecker4.simulation.waveFunctionCollapse.utils.WfcDebug

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable


/** Sample grid derived from a seed image. The [[BufferedImage]] is not retained. */
class PatternSamples private (
  private val smx: Int,
  private val smy: Int,
  private val sample: Array[ByteArray],
  val colors: Seq[Color],
  private val N: Int
) {

  def getPatternSymmetries(x: Int, y: Int, symmetry: Int = 8): Array[ByteArray] = {
    require(symmetry >= 1 && symmetry <= 8)
    val ps: Array[ByteArray] = Array.ofDim(symmetry)
    ps(0) = patternFromSample(x, y)
    if (symmetry > 1) ps(1) = reflect(ps(0))
    if (symmetry > 2) {
      ps(2) = rotate(ps(0))
      ps(3) = reflect(ps(2))
    }
    if (symmetry > 4) {
      ps(4) = rotate(ps(2))
      ps(5) = reflect(ps(4))
      ps(6) = rotate(ps(4))
      ps(7) = reflect(ps(6))
    }
    ps
  }

  private def rotate(p: ByteArray): ByteArray =
    pattern((x, y) => p(N - 1 - y + x * N))

  private def reflect(p: ByteArray): ByteArray =
    pattern((x, y) => p(N - 1 - x + y * N))

  private def patternFromSample(x: Int, y: Int): ByteArray =
    pattern((dx, dy) => sample((x + dx) % smx)((y + dy) % smy))

  private def pattern(passedInFunc: (Int, Int) => Byte): ByteArray = {
    val result: ByteArray = new Array(N * N)
    for (y <- 0 until N; x <- 0 until N)
      result(x + y * N) = passedInFunc(x, y)
    result
  }
}

object PatternSamples {
  def apply(bitmap: BufferedImage, N: Int): PatternSamples = {
    val smx = bitmap.getWidth
    val smy = bitmap.getHeight
    val sample: Array[ByteArray] = Array.ofDim[Byte](smx, smy)
    val start1 = System.currentTimeMillis()
    val palette = mutable.ArrayBuffer[Color]()
    val colorToIndex = mutable.HashMap[Color, Int]()
    for (y <- 0 until smy; x <- 0 until smx) {
      val color = new Color(bitmap.getRGB(x, y))
      val i = colorToIndex.getOrElseUpdate(color, {
        val idx = palette.size
        palette += color
        idx
      })
      sample(x)(y) = i.toByte
    }
    if (WfcDebug.enabled)
      println("done calc sample in " + (System.currentTimeMillis() - start1) / 1000.0)
    new PatternSamples(smx, smy, sample, palette.toSeq, N)
  }
}
