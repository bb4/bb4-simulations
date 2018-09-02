// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import java.awt._


/**
  * Given a depth, and a Graphics2 instance, set appropriate color and style.
  * @author Barry Becker
  */
object GraphicsStyler {
  private val LINE_COLORS = Array(
    new Color(0, 0, 80, 100),
    new Color(0, 10, 210, 200),
    new Color(0, 200, 90, 255),
    new Color(80, 255, 0, 160),
    new Color(250, 200, 0, 150),
    new Color(255, 0, 0, 100),
    new Color(255, 0, 100, 70),
    new Color(250, 0, 255, 40)
  )
}

class GraphicsStyler(/** max line width at depth 0 */
                     var lineWidth: Float) {
  def setLineWidth(lineWidth: Float): Unit = {
    this.lineWidth = lineWidth
  }

  def setStyle(depth: Int, g2: Graphics2D): Unit = {
    val stroke = new BasicStroke(lineWidth / (3 * depth + 1.0f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    g2.setStroke(stroke)
    g2.setColor(GraphicsStyler.LINE_COLORS(Math.min(depth, GraphicsStyler.LINE_COLORS.length - 1)))
  }
}