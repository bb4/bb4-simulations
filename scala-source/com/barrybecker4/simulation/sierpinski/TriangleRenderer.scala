// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import java.awt.Graphics2D
import scala.compiletime.uninitialized


/**
  * Draws a Sierpinski triangle to a specified depth (recursive subdivision).
  * @author Barry Becker
  */
class TriangleRenderer(var styler: GraphicsStyler) {
  private var maxDepth = 1
  private var g2: Graphics2D = uninitialized

  def setDepth(depth: Int): Unit = {
    require(depth > 0 && depth < 20, s"Unreasonable max depth of $depth specified.")
    maxDepth = depth
  }

  def render(triangle: Triangle, g2: Graphics2D): Unit = {
    this.g2 = g2
    draw(triangle, 0)
  }

  private def draw(triangle: Triangle, depth: Int): Unit = {
    styler.setStyle(depth, g2)
    drawTriangle(triangle, fill = false)
    val (inner, nearA, nearB, nearC) = Triangle.sierpinskiSubdivision(triangle)
    if depth >= maxDepth then drawTriangle(inner, fill = true)
    else
      draw(nearA, depth + 1)
      draw(nearB, depth + 1)
      draw(nearC, depth + 1)
  }

  private def drawTriangle(triangle: Triangle, fill: Boolean): Unit = {
    val poly = triangle.getPoly
    if fill then g2.fillPolygon(poly)
    else g2.drawPolygon(poly)
  }
}
