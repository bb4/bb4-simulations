/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph

import com.barrybecker4.simulation.spirograph.model.Parameters
import java.awt._


/**
  * Draws the Axes, circles, and spoke.
  * The circles and spoke represent the plastic stylus tool of the old spirograph.
  * @author Barry Becker
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

  /** Draw axes */
  private def drawAxes(g: Graphics2D): Unit = {
    g.setColor(DecorationRenderer.AXES_COLOR)
    g.setStroke(DecorationRenderer.AXES_STROKE)
    g.drawLine(width >> 1, 0, width >> 1, height)
    g.drawLine(0, height >> 1, width, height >> 1)
  }

  /** Draw central circle. */
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
    val center = params.getCenter(width, height)
    val sign = params.sign
    val r2 = params.r2
    g.drawOval((center.getX - sign * r2).toInt, (center.getY - sign * r2).toInt, (2 * sign * r2).toInt, (2 * sign * r2).toInt)
    g.setColor(DecorationRenderer.CIRCLE2_FILL_COLOR)
    g.fillOval((center.getX - sign * r2).toInt, (center.getY - sign * r2).toInt, (2 * sign * r2).toInt, (2 * sign * r2).toInt)
  }

  private def drawLineAndDot(g: Graphics2D): Unit = {
    g.setColor(DecorationRenderer.CIRCLE_COLOR)
    val center = params.getCenter(width, height)
    var side = params.sign
    val pos = params.pos
    val r2 = params.r2
    val phi = params.phi
    val dotPos = new Point((center.getX + pos * Math.cos(phi)).toInt, (center.getY - pos * Math.sin(phi)).toInt)
    if (pos < 0) side = -side
    g.setStroke(DecorationRenderer.SPOKE_STROKE)
    if (params.y > 0) {
      val startX = (center.getX + side * r2 * Math.cos(phi)).toInt
      val startY = (center.getY - side * r2 * Math.sin(phi)).toInt
      g.drawLine(startX, startY, dotPos.x, dotPos.y)
    }
    drawDot(g, dotPos)
  }

  private def drawDot(g: Graphics2D, dotPos: Point): Unit = {
    g.fillOval(dotPos.x - DecorationRenderer.HALF_DOT_RAD, dotPos.y - DecorationRenderer.HALF_DOT_RAD,
      DecorationRenderer.DOT_RAD, DecorationRenderer.DOT_RAD)
  }
}