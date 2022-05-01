// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.trebuchet.rendering

import com.barrybecker4.simulation.common.PhysicsConstants
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.{HEIGHT, SCALE_FACTOR}
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
    val cos = SCALE_FACTOR * cwLeverLength * Math.cos(counterWeight.getAngle)
    val sin = SCALE_FACTOR * cwLeverLength * Math.sin(counterWeight.getAngle)
    val attachPt = new Vector2d(counterWeight.lever.getStrutBaseX + sin, (-SCALE_FACTOR * HEIGHT).toInt - cos)
    val y = viewHeight - counterWeight.lever.getBaseY

    g2.drawLine(
      (scale * attachPt.x).toInt, (y + scale * attachPt.y).toInt,
      (scale * attachPt.x).toInt, (y + scale * (attachPt.y + counterWeight.getWeightHangLength)).toInt
    )
    val radius = (SCALE_FACTOR * 0.05 * Math.cbrt(counterWeight.getMass)).toInt
    g2.setColor(COLOR)
    val diameter = (scale * 2.0 * radius).toInt
    val xOval = (scale * (attachPt.x - radius)).toInt
    val yOval = (y + scale * (attachPt.y + counterWeight.getWeightHangLength)).toInt
    g2.drawOval(xOval, yOval, diameter, diameter)
    g2.setColor(FILL_COLOR)
    g2.fillOval(xOval, yOval, diameter, diameter)
    val bottomY = attachPt.y + counterWeight.getWeightHangLength + diameter

    if (showVelocityVectors) {
      g2.setStroke(VELOCITY_VECTOR_STROKE)
      g2.setColor(VELOCITY_VECTOR_COLOR)
      val velocityMagnitude = counterWeight.lever.getCounterWeightLeverLength * counterWeight.getAngularVelocity * Math.sin(counterWeight.getAngle)
      g2.drawLine((scale * attachPt.x).toInt, (scale * bottomY + y).toInt,
        (scale * attachPt.x).toInt, (scale * (bottomY + velocityMagnitude) + y).toInt)
    }
    if (showForceVectors) {
      g2.setStroke(FORCE_VECTOR_STROKE)
      g2.setColor(FORCE_VECTOR_COLOR)
      g2.drawLine((scale * attachPt.x).toInt, (scale * bottomY + y).toInt, 
        (scale * attachPt.x).toInt, (scale * (bottomY + PhysicsConstants.GRAVITY * counterWeight.getMass) + y).toInt)
    }
  }
}
