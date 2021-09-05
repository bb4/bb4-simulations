// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors

import com.barrybecker4.simulation.waveFunctionCollapse.model.DoubleArray
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave

import java.awt.Color
import java.awt.image.BufferedImage


class SimpleTileImageExtractor(
  FMX: Int, FMY: Int,
  tCounter: Int,
  tilesize: Int,
  tiles: Seq[Array[Color]],
  weights: DoubleArray,
  black: Boolean
) extends ImageExtractor {


  def getImage(wave: Wave): BufferedImage = {
    val result = new BufferedImage(FMX * tilesize, FMY * tilesize, BufferedImage.TYPE_4BYTE_ABGR)

    if (wave.hasObserved) {
      for (x <- 0 until FMX) {
        for (y <- 0 until FMY) {
          val tile: Array[Color] = tiles(wave.get(x + y * FMX).observed)
          for (yt <- 0 until tilesize) {
            for (xt <- 0 until tilesize) {
              val c = tile(xt + yt * tilesize)
              result.setRGB(x * tilesize + xt, y * tilesize + yt, c.getRGB)
            }
          }
        }
      }
    }
    else {
      for (x <- 0 until FMX) {
        for (y <- 0 until FMY) {
          val waveCell = wave.get(x + y * FMX)
          val a = waveCell.enabled

          val amount = a.map(a => if (a) 1 else 0).sum
          val lambda = 1.0 / (0 until tCounter).filter(t => a(t)).map(t => weights(t)).sum
          for (yt <- 0 until tilesize) {
            for (xt <- 0 until tilesize) {
              if (black && amount == tCounter) {
                val blackColor = Color.black
                result.setRGB(x * tilesize + xt, y * tilesize + yt, blackColor.getRGB)
              } else {
                var r = 0.0
                var g = 0.0
                var b = 0.0
                for (t <- 0 until tCounter) {
                  if (waveCell.enabled(t)) {
                    val c = tiles(t)(xt + yt * tilesize)
                    r += c.getRed.toDouble * weights(t) * lambda
                    g += c.getGreen.toDouble * weights(t) * lambda
                    b += c.getBlue.toDouble * weights(t) * lambda
                  }
                }

                val color = new Color(trimToColor(r.toInt), trimToColor(g.toInt), trimToColor(b.toInt))
                val xCord = x * tilesize + xt
                val yCord = y * tilesize + yt
                result.setRGB(xCord, yCord, color.getRGB)
              }
            }
          }
        }
      }
    }
    result
  }

  private def trimToColor(c: Int): Int = if (c <= 255) c else 255
}
