// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.trebuchet.rendering

import com.barrybecker4.simulation.common.PhysicsConstants
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.HEIGHT
import com.barrybecker4.simulation.trebuchet.model.parts.CounterWeight
import com.barrybecker4.simulation.trebuchet.rendering.CounterWeightRenderer.{COLOR, FILL_COLOR, STROKE}
import com.barrybecker4.simulation.trebuchet.rendering.RenderingConstants.{FORCE_VECTOR_COLOR, FORCE_VECTOR_STROKE, VELOCITY_VECTOR_COLOR, VELOCITY_VECTOR_STROKE, showForceVectors, showVelocityVectors}

import java.awt.{BasicStroke, Color, Graphics2D}
import javax.vecmath.Vector2d


object CounterWeightRenderer {
  private val STROKE = new BasicStroke(4.0f)
  private val COLOR = new Color(120, 40, 40)
  private val FILL_COLOR = new Color(190, 180, 140)
}

class CounterWeightRenderer(counterWeight: CounterWeight) extends AbstractPartRenderer {


  override def render(g2: Graphics2D, scale: Double, viewHeight: Int): Unit = {
    g2.setStroke(STROKE)
    g2.setColor(COLOR)
    val cwLeverLength = counterWeight.lever.getCounterWeightLeverLength
    val cos = cwLeverLength * Math.cos(counterWeight.getLeverAngle)
    val sin = cwLeverLength * Math.sin(counterWeight.getLeverAngle)
    val attachPt = new Vector2d(counterWeight.lever.getStrutBaseX + sin, -HEIGHT.toInt - cos)
    val y = viewHeight - scale * counterWeight.lever.getBaseY

    val radius = counterWeight.getRadius
    val diameter = (scale * 2.0 * radius).toInt

    drawCounterWightCable(g2, scale, attachPt, y)
    drawCounterWeight(g2, scale, attachPt, y, diameter)

    val bottomY = scale * (attachPt.y + counterWeight.getWeightHangLength) + diameter

    if (showVelocityVectors) {
      drawVelocityVector(g2, scale, attachPt, y, bottomY)
    }
    if (showForceVectors) {
      drawForceVector(g2, scale, attachPt, y, bottomY)
    }
  }

  private def drawCounterWightCable(g2: Graphics2D, scale: Double, attachPt: Vector2d, y: Double): Unit = {
    g2.drawLine(
      (scale * attachPt.x).toInt, (y + scale * attachPt.y).toInt,
      (scale * attachPt.x).toInt, (y + scale * (attachPt.y + counterWeight.getWeightHangLength)).toInt
    )
  }

  private def drawCounterWeight(g2: Graphics2D, scale: Double, attachPt: Vector2d, y: Double, diameter: Int): Unit = {
    g2.setColor(COLOR)
    val xOval = (scale * attachPt.x).toInt - diameter / 2
    val yOval = (y + scale * (attachPt.y + counterWeight.getWeightHangLength)).toInt
    g2.drawOval(xOval, yOval, diameter, diameter)
    g2.setColor(FILL_COLOR)
    g2.fillOval(xOval, yOval, diameter, diameter)

  }
  private def drawVelocityVector(g2: Graphics2D, scale: Double, attachPt: Vector2d, y: Double, bottomY: Double): Unit = {
    g2.setStroke(VELOCITY_VECTOR_STROKE)
    g2.setColor(VELOCITY_VECTOR_COLOR)
    val velocityMagnitude = counterWeight.lever.getCounterWeightLeverLength * counterWeight.getLeverAngularVelocity * Math.sin(counterWeight.getLeverAngle)
    g2.drawLine((scale * attachPt.x).toInt, (y + bottomY).toInt,
      (scale * attachPt.x).toInt, (y + bottomY + velocityMagnitude).toInt)
  }

  private def drawForceVector(g2: Graphics2D, scale: Double, attachPt: Vector2d, y: Double, bottomY: Double): Unit = {
    g2.setStroke(FORCE_VECTOR_STROKE)
    g2.setColor(FORCE_VECTOR_COLOR)
    g2.drawLine((scale * attachPt.x).toInt, (y + bottomY).toInt,
      (scale * attachPt.x).toInt, (bottomY + PhysicsConstants.GRAVITY * counterWeight.getMass + y).toInt)
  }
}
