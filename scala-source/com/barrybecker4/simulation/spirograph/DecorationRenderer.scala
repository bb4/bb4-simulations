/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph

import com.barrybecker4.simulation.spirograph.model.Parameters
import com.barrybecker4.simulation.spirograph.model.SpirographMath
import java.awt._


/**
  * Draws the Axes, circles, and spoke.
  * The circles and spoke represent the plastic stylus tool of the old spirograph.
  */
object DecorationRenderer {
  private val AXES_COLOR = new Color(120, 120, 200)
  private val CIRCLE_COLOR = new Color(120, 0, 220)
  private val CIRCLE2_FILL_COLOR = new Color(100, 0, 200, 70)
  private val AXES_STROKE = new BasicStroke(1)
  private val CIRCLE_STROKE = new BasicStroke(2)
  private val SPOKE_STROKE = new BasicStroke(3)
  private val DOT_RAD = 7
  private val HALF_DOT_RAD = 3
}

class DecorationRenderer(var params: Parameters) {
  private var width = 0
  private var height = 0

  def drawDecoration(g: Graphics2D, width: Int, height: Int): Unit = {
    this.width = width
    this.height = height
    drawAxes(g)
    drawCentralCircle(g)
    drawCircleAndDot(g)
  }

  private def drawAxes(g: Graphics2D): Unit = {
    g.setColor(DecorationRenderer.AXES_COLOR)
    g.setStroke(DecorationRenderer.AXES_STROKE)
    g.drawLine(width >> 1, 0, width >> 1, height)
    g.drawLine(0, height >> 1, width, height >> 1)
  }

  private def drawCentralCircle(g: Graphics2D): Unit = {
    g.setColor(DecorationRenderer.CIRCLE_COLOR)
    g.setStroke(DecorationRenderer.CIRCLE_STROKE)
    val r1 = params.r1
    g.drawOval(((width >> 1) - r1).toInt, ((height >> 1) - r1).toInt, (2 * r1).toInt, (2 * r1).toInt)
  }

  private def drawCircleAndDot(g: Graphics2D): Unit = {
    g.setColor(DecorationRenderer.CIRCLE_COLOR)
    drawCircle2(g)
    drawLineAndDot(g)
  }

  private def drawCircle2(g: Graphics2D): Unit = {
    val (cx, cy) = SpirographMath.center(width, height, params.r1, params.r2, params.theta)
    val sign = params.sign
    val r2 = params.r2
    val ox = (cx - sign * r2).toInt
    val oy = (cy - sign * r2).toInt
    val dim = (2 * sign * r2).toInt
    g.setColor(DecorationRenderer.CIRCLE_COLOR)
    g.drawOval(ox, oy, dim, dim)
    g.setColor(DecorationRenderer.CIRCLE2_FILL_COLOR)
    g.fillOval(ox, oy, dim, dim)
  }

  private def drawLineAndDot(g: Graphics2D): Unit = {
    g.setColor(DecorationRenderer.CIRCLE_COLOR)
    val (cx, cy) = SpirographMath.center(width, height, params.r1, params.r2, params.theta)
    var side = params.sign
    val pos = params.pos
    val r2 = params.r2
    val phi = params.phi
    val (px, py) = SpirographMath.penPoint(cx, cy, pos, phi)
    val dotPos = new Point(px.toInt, py.toInt)
    if (pos < 0) side = -side
    g.setStroke(DecorationRenderer.SPOKE_STROKE)
    if (params.y > 0) {
      val startX = (cx + side * r2 * Math.cos(phi)).toInt
      val startY = (cy - side * r2 * Math.sin(phi)).toInt
      g.drawLine(startX, startY, dotPos.x, dotPos.y)
    }
    drawDot(g, dotPos)
  }

  private def drawDot(g: Graphics2D, dotPos: Point): Unit = {
    g.fillOval(dotPos.x - DecorationRenderer.HALF_DOT_RAD, dotPos.y - DecorationRenderer.HALF_DOT_RAD,
      DecorationRenderer.DOT_RAD, DecorationRenderer.DOT_RAD)
  }
}
