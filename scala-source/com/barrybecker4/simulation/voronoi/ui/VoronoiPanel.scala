// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.ui

import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor
import com.barrybecker4.simulation.voronoi.rendering.VoronoiRenderer
import com.barrybecker4.simulation.voronoi.ui.VoronoiPanel.{HEIGHT, MARGIN, WIDTH}

import javax.swing.JPanel
import java.awt.Dimension
import java.awt.Graphics


object VoronoiPanel {
  val WIDTH = 1024
  val HEIGHT = 1024
  val MARGIN = 120
}

class VoronoiPanel(val numPoints: Int) extends JPanel {
  setPreferredSize(new Dimension(WIDTH + 2 * MARGIN, HEIGHT + 2 * MARGIN))
  private val renderer = new VoronoiRenderer(WIDTH, HEIGHT, this)
  private val points = new PointGenerator().generatePoints(numPoints)


  def start(): Unit = {
    val v = new VoronoiProcessor(points, Some(renderer))
    val edgeList = v.getEdgeList
    renderer.show(points, edgeList)
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
      super.paint(g)
    g.drawImage(renderer.getImage, 0, 0, null)
  }
}
