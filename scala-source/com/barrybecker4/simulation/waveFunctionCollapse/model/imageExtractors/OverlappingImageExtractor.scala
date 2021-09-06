// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors

import com.barrybecker4.simulation.waveFunctionCollapse.model.ByteArray
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave

import java.awt.Color
import java.awt.image.BufferedImage

class OverlappingImageExtractor(
  FMX: Int, FMY: Int, N: Int,
  tCounter: Int,
  patterns: Array[ByteArray],
  colors: Seq[Color],
  onBoundary: (Int, Int) => Boolean
) extends ImageExtractor {


  override def getImage(wave: Wave): BufferedImage = {
    val result = new BufferedImage(FMX, FMY, BufferedImage.TYPE_4BYTE_ABGR)
    if (wave.hasObserved) {
      for (y <- 0 until FMY) {
        val dy = if (y < FMY - N + 1) 0 else N - 1
        for (x <- 0 until FMX) {
          val dx = if (x < FMX - N + 1) 0 else N - 1
          val isObserved = wave.get(x - dx + (y - dy) * FMX).observed
          val c = colors(patterns(isObserved)(dx + dy * N).toInt)
          result.setRGB(x, y, c.getRGB)
        }
      }
    }
    else {
      for (i <- 0 until wave.size()) {
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
              val c = if (contributors == 0) Color.BLACK
                      else new Color(r / contributors, g / contributors, b / contributors)
              result.setRGB(x, y, c.getRGB)
            }
          }
        }
      }
    }
    result
  }
}
