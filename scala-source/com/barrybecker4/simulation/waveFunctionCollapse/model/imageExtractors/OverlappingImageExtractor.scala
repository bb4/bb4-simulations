// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors

import com.barrybecker4.simulation.waveFunctionCollapse.model.ByteArray
import com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors.OverlappingImageExtractor.FILL_COLOR
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave

import java.awt.{Color, Dimension}
import java.awt.image.BufferedImage

object OverlappingImageExtractor {
  val FILL_COLOR: Color = new Color(20, 50, 140)
}

class OverlappingImageExtractor(
  dims: Dimension, N: Int,
  tCounter: Int,
  patterns: Array[ByteArray],
  colors: Seq[Color],
  scale: Int,
  onBoundary: (Int, Int) => Boolean
) extends ImageExtractor {

  override def getImage(wave: Wave): BufferedImage = {

    val start = System.currentTimeMillis()
    val result = new BufferedImage(dims.width * scale, dims.height * scale, BufferedImage.TYPE_4BYTE_ABGR)

    if (wave.hasObserved) populateObservedImage(wave, result)
    else populateUnobservedImage(wave, result)

    println("overlap image extracted in " + (System.currentTimeMillis() - start) / 1000.0)
    result
  }

  private def populateObservedImage(wave: Wave, result: BufferedImage): Unit = {
    val FMX = dims.width
    val FMY = dims.height
    for (y <- 0 until FMY) {
      val dy = if (y < FMY - N + 1) 0 else N - 1
      for (x <- 0 until FMX) {
        val dx = if (x < FMX - N + 1) 0 else N - 1
        val isObserved = wave.get(x - dx + (y - dy) * FMX).observed
        val c = colors(patterns(isObserved)(dx + dy * N).toInt)
        setPixel(x, y, c, result)
      }
    }
  }

  private def populateUnobservedImage(wave: Wave, result: BufferedImage): Unit = {
    for (i <- 0 until wave.size()) {
      colorPoint(i, wave, result)
    }
  }

  private def colorPoint(i: Int, wave: Wave, result: BufferedImage): Unit = {
    val FMX = dims.width
    val FMY = dims.height
    var contributors = 0
    var r = 0
    var g = 0
    var b = 0

    val x = i % FMX
    val y = i / FMX

    for (dy <- 0 until N) {
      for (dx <- 0 until N) {

        var sx = x - dx
        if (sx < 0) sx += FMX

        var sy = y - dy
        if (sy < 0) sy += FMY

        val s = sx + sy * FMX

        if (!onBoundary(sx, sy)){
          for (t <- 0 until tCounter) {
            if (wave.get(s).enabled(t)) {
              contributors += 1
              val color = colors(patterns(t)(dx + dy * N).toInt)
              r += color.getRed
              g += color.getGreen
              b += color.getBlue
            }
          }
          val c = if (contributors == 0) FILL_COLOR
          else new Color(r / contributors, g / contributors, b / contributors)
          setPixel(x, y, c, result)
        }
      }
    }
  }

  private def setPixel(x: Int, y: Int, c: Color, result: BufferedImage): Unit = {
    for (i <- 0 until scale)
      for (j <- 0 until scale)
        result.setRGB(scale * x + i, scale * y + j, c.getRGB)
  }
}
