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
  private var styler: GraphicsStyler = new GraphicsStyler(SierpinskiRenderer.DEFAULT_LINE_WIDTH.toFloat)
  private var triangleRenderer = new TriangleRenderer(styler)
  private var width = 0
  private var height = 0
  private var g2: Graphics2D = _


  def setDepth(depth: Int): Unit = triangleRenderer.setDepth(depth)
  def setLineWidth(width: Float): Unit = styler.setLineWidth(width)

  def setSize(width: Int, height: Int): Unit = {
    this.width = width
    this.height = height
  }

  /** draw the sierpinski triangle */
  def paint(g: Graphics): Unit = {
    g2 = g.asInstanceOf[Graphics2D]
    clear()
    val A = new Point(width / 2, SierpinskiRenderer.MARGIN)
    val B = new Point(SierpinskiRenderer.MARGIN, height - SierpinskiRenderer.MARGIN)
    val C = new Point(width - 2 * SierpinskiRenderer.MARGIN, height - SierpinskiRenderer.MARGIN)
    val triangle = new Triangle(A, B, C)
    triangleRenderer.render(triangle, g2)
  }

  /** erase everything so we can start anew. */
  private def clear(): Unit = {
    g2.setBackground(SierpinskiRenderer.BACKGROUND_COLOR)
    g2.clearRect(0, 0, width, height)
    // this smooths the lines when we draw.
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  }
}