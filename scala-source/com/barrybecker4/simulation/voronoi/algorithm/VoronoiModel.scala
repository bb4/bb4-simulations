// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap

import java.awt.*
import java.awt.image.BufferedImage
import javax.vecmath.Point2d


/**
  * Everything we need to know to compute the henon phase diagram.
  * @author Barry Becker
  */
class VoronoiModel private[algorithm](val width: Int, val height: Int,
                                    var params: PoissonParams, val usePoissonDistribution: Boolean, val connectPoints: Boolean,
                                    var numPoints: Int, var cmap: ColorMap) {

  final private var points: Array[Point2d] = _

  /** offline rendering is fast  */
  final private var offlineGraphics = new OfflineGraphics(new Dimension(width, height), Color.BLACK)
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get
  def getWidth: Int = width
  def getHeight: Int = height

  def reset(): Unit = synchronized {
    points = Array.ofDim[Point2d](numPoints)

    for (i <- 0 until numPoints) {
      if (usePoissonDistribution) {
        //val color = cmap.getColorForValue(xpos)
        points(i) = new Point2d(Math.random(), Math.random())
      }
      else {
        val randXPos = Math.random
        //val color = cmap.getColorForValue(randXPos)
        points(i) = new Point2d(Math.random(), Math.random())
      }
    }
  }

  /** @param numSteps number of steps to increment each traveler */
  def increment(numSteps: Int): Unit = synchronized {

    var lastPoint = points(0)
    for (point <- points) {
      offlineGraphics.setColor(Color.WHITE)
      val xpos = (width * point.x).toInt
      val ypos = (height * point.y).toInt
      if (connectPoints) {
        val xposLast = (width * lastPoint.x).toInt
        val yposLast = (height * lastPoint.y).toInt
        offlineGraphics.drawLine(xposLast, yposLast, xpos, ypos)
      }
      else offlineGraphics.fillCircle(xpos, ypos, 3)
      lastPoint = point
    }
  }
}
