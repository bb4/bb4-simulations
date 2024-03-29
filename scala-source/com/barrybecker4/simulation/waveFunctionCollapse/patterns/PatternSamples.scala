// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.patterns

import com.barrybecker4.simulation.waveFunctionCollapse.model.ByteArray

import java.awt.Color
import java.awt.image.BufferedImage


case class PatternSamples(bitmap: BufferedImage, N: Int) {

  private val smx: Int = bitmap.getWidth
  private val smy: Int = bitmap.getHeight
  private val sample: Array[ByteArray] = Array.ofDim[Byte](smx, smy)
  val colors: Seq[Color] = initialize(bitmap)

  private def initialize(bitmap: BufferedImage): Seq[Color] = {
    val start1 = System.currentTimeMillis()
    var colors: Seq[Color] = Seq()
    var i = 0
    for (y <- 0 until smy) {
      for (x <- 0 until smx) {
        val color = new Color(bitmap.getRGB(x, y))
        i = colors.indexOf(color)
        if (i == -1) {
          i = colors.length
          colors :+= color
        }
        assert(i >= 0)
        sample(x)(y) = i.toByte
      }
    }
    println("done calc sample in " + (System.currentTimeMillis() - start1) / 1000.0)
    colors
  }

  def getPatternSymmetries(x: Int, y: Int): Array[ByteArray] = {
    val ps: Array[ByteArray] = Array.fill(8)(null)
    ps(0) = patternFromSample(x, y)
    ps(1) = reflect(ps(0))
    ps(2) = rotate(ps(0))
    ps(3) = reflect(ps(2))
    ps(4) = rotate(ps(2))
    ps(5) = reflect(ps(4))
    ps(6) = rotate(ps(4))
    ps(7) = reflect(ps(6))
    ps
  }

  private def rotate(p: ByteArray): ByteArray = {
    pattern((x, y) => p(N - 1 - y + x * N))
  }

  private def reflect(p: ByteArray): ByteArray = {
    pattern((x, y) => p(N - 1 - x + y * N))
  }

  private def patternFromSample(x: Int, y: Int): ByteArray = {
    pattern((dx, dy) => sample((x + dx) % smx)((y + dy) % smy))
  }

  private def pattern(passedInFunc: (Int, Int) => Byte): ByteArray = {
    val result: ByteArray = new Array(N * N)
    for (y <- 0 until N)
      for (x <- 0 until N)
        result(x + y * N) = passedInFunc(x, y)
    result
  }
}
