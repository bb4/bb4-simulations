// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.rendering

import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer.{POINT_COLOR, POINT_RADIUS, STROKE}
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.Color
import javax.vecmath.Point2d
import java.awt.*
import java.awt.image.BufferedImage


object VoronoiRenderer {
  private val POINT_RADIUS = 1
  private val POINT_COLOR = Color.YELLOW
  private val STROKE = new BasicStroke(0.4)
}

class VoronoiRenderer(width: Int, height: Int) {

  /** offline rendering is fast  */
  final private var offlineGraphics = new OfflineGraphics(new Dimension(width, height), Color.BLACK)
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def render(samples: IndexedSeq[Point2d]): Unit = {
    var lastPoint = samples(0)
    offlineGraphics.setStroke(STROKE)
    offlineGraphics.setColor(POINT_COLOR)

    for (point <- samples) {
      val xpos = point.x.toInt
      val ypos = point.y.toInt
      offlineGraphics.fillCircle(xpos, ypos, POINT_RADIUS)
      lastPoint = point
    }
  }

}
