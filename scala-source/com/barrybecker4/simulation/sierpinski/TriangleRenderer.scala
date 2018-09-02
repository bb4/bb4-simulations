// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import java.awt.Graphics2D
import java.awt.Point


object TriangleRenderer {
  private val FILL = false
}

/**
  * This class draws a Sierpinski triangle to a specified depth.
  * @author Barry Becker
  */
class TriangleRenderer(var styler: GraphicsStyler) {
  private var maxDepth = 1
  private var g2: Graphics2D = _

  def setDepth(depth: Int): Unit = {
    assert(depth > 0 && depth < 20, "Unreasonable max depth of " + depth + " specified.")
    maxDepth = depth
  }

  /** Recursive method to actually draw the algorithm
    * This is the secret sauce of the whole application.
    */
  def render(triangle: Triangle, g2: Graphics2D): Unit = {
    this.g2 = g2
    draw(triangle, 0)
  }

  /** Recursive method to actually draw the algorithm.
    * This is the secret sauce of the whole application.
    */
  private def draw(triangle: Triangle, depth: Int): Unit = {
    styler.setStyle(depth, g2)
    drawTriangle(triangle)
    val a = midpoint(triangle.B, triangle.C)
    val b = midpoint(triangle.A, triangle.C)
    val c = midpoint(triangle.B, triangle.A)
    if (depth >= maxDepth) drawTriangle(new Triangle(a, b, c), true)
    else {
      draw(new Triangle(triangle.A, c, b), depth + 1)
      draw(new Triangle(c, triangle.B, a), depth + 1)
      draw(new Triangle(b, a, triangle.C), depth + 1)
    }
  }

  private def midpoint(P1: Point, P2: Point) = new Point((P1.x + P2.x) / 2, (P1.y + P2.y) / 2)

  private def drawTriangle(triangle: Triangle): Unit = {
    drawTriangle(triangle, TriangleRenderer.FILL)
  }

  private def drawTriangle(sTriangle: Triangle, fill: Boolean): Unit = {
    val triangle = sTriangle.getPoly
    if (fill) g2.fillPolygon(triangle)
    else g2.drawPolygon(triangle)
  }
}