// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors

import com.barrybecker4.simulation.waveFunctionCollapse.model.{DoubleArray, WfcModel}
import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.{Wave, WaveCell}

import java.awt.{Color, Dimension}
import java.awt.image.BufferedImage


class SimpleTiledImageExtractor(
  dims: Dimension,
  tCounter: Int,
  tilesize: Int,
  tiles: Seq[Array[Color]],
  weights: DoubleArray,
  black: Boolean
) extends ImageExtractor {

  def getImage(wave: Wave): BufferedImage = {
    val FMX = dims.width
    val FMY = dims.height
    assert(FMX > 0 && FMY > 0 && tilesize > 0, s"FMX=$FMX FMY=$FMY tilesize=$tilesize")
    val start = System.currentTimeMillis()
    val result = new BufferedImage(FMX * tilesize, FMY * tilesize, BufferedImage.TYPE_4BYTE_ABGR)

    if (wave.hasObserved) populateObservedImage(wave, result)
    else populateUnobservedImage(wave, result)

    println("tile image extracted in " + (System.currentTimeMillis() - start) / 1000.0)
    result
  }

  private def populateObservedImage(wave: Wave, result: BufferedImage): Unit = {
    val FMX = dims.width
    val FMY = dims.height
    for (x <- 0 until FMX) {
      for (y <- 0 until FMY) {
        populateObservedTile(x, y, tiles(wave.get(x + y * FMX).observed), result)
      }
    }
  }

  private def populateObservedTile(x: Int, y: Int, tile: Array[Color], result: BufferedImage): Unit = {
    for (yt <- 0 until tilesize) {
      for (xt <- 0 until tilesize) {
        val c = tile(xt + yt * tilesize)
        result.setRGB(x * tilesize + xt, y * tilesize + yt, c.getRGB)
      }
    }
  }

  private def populateUnobservedImage(wave: Wave, result: BufferedImage): Unit = {
    val FMX = dims.width
    val FMY = dims.height
    for (x <- 0 until FMX) {
      for (y <- 0 until FMY) {
        val waveCell = wave.get(x + y * FMX)
        assert(waveCell != null, "waveCell was null")
        populateUnobservedTile(x, y, waveCell, result)
      }
    }
  }

  private def populateUnobservedTile(x: Int, y: Int, waveCell: WaveCell, result: BufferedImage): Unit = {
    val enabled = waveCell.enabled

    val amount = enabled.map(a => if (a) 1 else 0).sum
    val lambda = 1.0 / (0 until tCounter).filter(t => enabled(t)).map(t => weights(t)).sum
    for (yt <- 0 until tilesize) {
      for (xt <- 0 until tilesize) {
        if (black && amount == tCounter) {
          val blackColor = Color.black
          result.setRGB(x * tilesize + xt, y * tilesize + yt, blackColor.getRGB)
        } else {
          fillPointColor(x, y, xt, yt, lambda, waveCell, result)
        }
      }
    }
  }

  private def fillPointColor(x: Int, y: Int, xt: Int, yt: Int,
                             lambda: Double, waveCell: WaveCell, result: BufferedImage): Unit = {
    val color = getColor(xt, yt, lambda, waveCell)
    val xCord = x * tilesize + xt
    val yCord = y * tilesize + yt
    result.setRGB(xCord, yCord, color.getRGB)
  }

  private def getColor(xt: Int, yt: Int, lambda: Double, waveCell: WaveCell): Color = {
    var r = 0.0
    var g = 0.0
    var b = 0.0
    def interp(channel: Int, t: Int): Double = channel * weights(t) * lambda

    for (t <- 0 until tCounter) {
      if (waveCell.enabled(t)) {
        val c = tiles(t)(xt + yt * tilesize)
        r += interp(c.getRed, t)
        g += interp(c.getGreen, t)
        b += interp(c.getBlue, t)
      }
    }
    new Color(trimToColor(r.toInt), trimToColor(g.toInt), trimToColor(b.toInt))
  }

  private def trimToColor(c: Int): Int = if (c <= 255) c else 255
}
