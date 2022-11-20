// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap

import java.awt.*
import java.awt.image.BufferedImage


/**
  * Everything we need to know to compute the henon phase diagram.
  * @author Barry Becker
  */
class VoronoiModel private[algorithm](val width: Int, val height: Int,
                                    var params: PoissonParams, val usePoissonDistribution: Boolean, val connectPoints: Boolean,
                                    var numTravelers: Int, var cmap: ColorMap) {

  final private var travelers: Array[Traveler] = _

  /** offline rendering is fast  */
  final private var offlineGraphics = new OfflineGraphics(new Dimension(width, height), Color.BLACK)
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get
  def getWidth: Int = width
  def getHeight: Int = height

  def reset(): Unit = synchronized {
    travelers = Array.ofDim[Traveler](numTravelers)
    val inc = 1.0 / numTravelers
    var xpos = 0.0
    for (i <- 0 until numTravelers) {
      if (usePoissonDistribution) {
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
    if (travelers == null) { // this should not happen, but it does sometimes
      System.err.println("travelers array was unexpectedly null. numTravelers = " + numTravelers)
      return
    }
    for (traveler <- travelers) {
      offlineGraphics.setColor(traveler.color)
      for (i <- 0 until numSteps) {
        val xpos = (width * (traveler.x / 2.0 + 0.5)).toInt
        val ypos = (height * (traveler.y / 2.0 + 0.5)).toInt
        if (connectPoints) {
          val xposLast = (width * (traveler.getLastX / 2.0 + 0.5)).toInt
          val yposLast = (height * (traveler.getLastY / 2.0 + 0.5)).toInt
          offlineGraphics.drawLine(xposLast, yposLast, xpos, ypos)
        }
        else offlineGraphics.drawPoint(xpos, ypos)
        traveler.increment()
      }
    }
  }
}
