// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.snake.geometry.Edge
import java.awt._


/**
  * Draws a snake edge (line geometry). It is modeled as a spring to simulate muscles.
  * @author Barry Becker
  */
object EdgeRenderer {
  private val EDGE_SCALE = 30.0
  /** show the edge different colors depending on percentage stretched  ( one being 100% stretched)  */
  private val stretchVals = Array(0.3, 0.9, 1.0, 1.1, 3.0)
  private val stretchColors = Array(
    new Color(255, 0, 0, 200),
    new Color(230, 120, 57, 250),
    new Color(50, 90, 60, 250),
    new Color(70, 120, 210, 200),
    new Color(10, 10, 255, 100))
  private val stretchColorMap = new ColorMap(stretchVals, stretchColors)
}

final class EdgeRenderer private[rendering](val renderParams: RenderingParameters) {

  def render(edge: Edge, g: Graphics2D): Unit = {
    g.setColor(EdgeRenderer.stretchColorMap.getColorForValue(edge.length / edge.restingLength))
    val ratio = edge.restingLength / edge.length
    val width = EdgeRenderer.EDGE_SCALE * Math.max(0, ratio - 0.95)
    val stroke = new BasicStroke(width.toFloat)
    g.setStroke(stroke)
    val scale = renderParams.scale

    val part1 = edge.firstParticle
    val part2 = edge.secondParticle
    val x1 = (scale * part1.x).toInt
    val y1 = (scale * part1.y).toInt
    val x2 = (scale * part2.x).toInt
    val y2 = (scale * part2.y).toInt
    //println("scale = " + scale + "  x1 = " + x1 + " x2=" + x2)
    g.drawLine(x1, y1, x2, y2)
  }
}
