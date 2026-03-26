// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import java.awt._


object SierpinskiRenderer {
  val DEFAULT_LINE_WIDTH = 23
  private val BACKGROUND_COLOR = new Color(255, 255, 255)
  private val MARGIN = 30
}

/**
  * This class draws the Sierpinski triangle.
  * @author Barry Becker
  */
class SierpinskiRenderer() {
  private val styler: GraphicsStyler = new GraphicsStyler(SierpinskiRenderer.DEFAULT_LINE_WIDTH.toFloat)
  private val triangleRenderer = new TriangleRenderer(styler)
  private var width = 0
  private var height = 0

  def setDepth(depth: Int): Unit = triangleRenderer.setDepth(depth)
  def setLineWidth(width: Float): Unit = styler.setLineWidth(width)

  def setSize(width: Int, height: Int): Unit = {
    this.width = width
    this.height = height
  }

  /** draw the sierpinski triangle */
  def paint(g: Graphics): Unit =
    g match
      case g2: Graphics2D =>
        clear(g2)
        val pa = new Point(width / 2, SierpinskiRenderer.MARGIN)
        val pb = new Point(SierpinskiRenderer.MARGIN, height - SierpinskiRenderer.MARGIN)
        val pc = new Point(width - 2 * SierpinskiRenderer.MARGIN, height - SierpinskiRenderer.MARGIN)
        val triangle = Triangle(pa, pb, pc)
        triangleRenderer.render(triangle, g2)
      case _ => ()

  /** erase everything so we can start anew. */
  private def clear(g2: Graphics2D): Unit = {
    g2.setBackground(SierpinskiRenderer.BACKGROUND_COLOR)
    g2.clearRect(0, 0, width, height)
    // this smooths the lines when we draw.
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  }
}
