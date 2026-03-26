// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase.algorithm

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.ui.renderers.OfflineGraphics
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import scala.compiletime.uninitialized


/**
  * Everything we need to know to compute the henon phase diagram.
  * @author Barry Becker
  */
class HenonModel private[algorithm](val width: Int, val height: Int,
                                    var params: TravelerParams, val uniformSeeds: Boolean, val connectPoints: Boolean,
                                    var numTravelers: Int, var cmap: ColorMap) {

  final private var travelers: Array[Traveler] = uninitialized

  /** offline rendering is fast  */
  final private var offlineGraphics = new OfflineGraphics(new Dimension(width, height), Color.BLACK)
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get
  def getWidth: Int = width
  def getHeight: Int = height

  /** Map normalized world coordinate in [-1, 1] to pixel x. */
  private def toPixelX(wx: Double): Int = (width * (wx / 2.0 + 0.5)).toInt

  /** Map normalized world coordinate in [-1, 1] to pixel y. */
  private def toPixelY(wy: Double): Int = (height * (wy / 2.0 + 0.5)).toInt

  def reset(): Unit = synchronized {
    travelers = Array.ofDim[Traveler](numTravelers)
    val inc = 1.0 / numTravelers
    var xpos = 0.0
    for (i <- 0 until numTravelers) {
      if (uniformSeeds) {
        val color = cmap.getColorForValue(xpos)
        travelers(i) = new Traveler(xpos, 0, color, params)
      }
      else {
        val randXPos = Math.random
        val color = cmap.getColorForValue(randXPos)
        travelers(i) = new Traveler(randXPos, 0, color, params)
      }
      xpos += inc
    }
  }

  /** @param numSteps number of steps to increment each traveler */
  def increment(numSteps: Int): Unit = synchronized {
    for (traveler <- travelers) {
      offlineGraphics.setColor(traveler.color)
      for (i <- 0 until numSteps) {
        val xpos = toPixelX(traveler.x)
        val ypos = toPixelY(traveler.y)
        if (connectPoints) {
          val xposLast = toPixelX(traveler.getLastX)
          val yposLast = toPixelY(traveler.getLastY)
          offlineGraphics.drawLine(xposLast, yposLast, xpos, ypos)
        }
        else offlineGraphics.drawPoint(xpos, ypos)
        traveler.increment()
      }
    }
  }
}
