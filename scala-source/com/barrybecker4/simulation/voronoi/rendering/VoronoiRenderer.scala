// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.rendering

import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer.POINT_RADIUS
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.Color
import javax.vecmath.Point2d
import java.awt.*
import java.awt.image.BufferedImage


object VoronoiRenderer {
  private val POINT_RADIUS = 1
}

class VoronoiRenderer(width: Int, height: Int) {

  /** offline rendering is fast  */
  final private var offlineGraphics = new OfflineGraphics(new Dimension(width, height), Color.BLACK)
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def render(samples: IndexedSeq[Point2d], connectPoints: Boolean): Unit = {
    var lastPoint = samples(0)
    for (point <- samples) {
      offlineGraphics.setColor(Color.WHITE)
      val xpos = point.x.toInt
      val ypos = point.y.toInt
      if (connectPoints) {
        val xposLast = lastPoint.x.toInt
        val yposLast = lastPoint.y.toInt
        offlineGraphics.drawLine(xposLast, yposLast, xpos, ypos)
      }
      else offlineGraphics.fillCircle(xpos, ypos, POINT_RADIUS) // RAD const
      lastPoint = point
    }
  }

}
