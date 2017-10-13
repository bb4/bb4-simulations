// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

import com.barrybecker4.simulation.common1.PhysicsConstants
import javax.vecmath.Vector2d
import java.awt._
import RenderablePart._


/**
  * @author Barry Becker Date: Sep 25, 2005
  */
object CounterWeight {
  private val WEIGHT_HANG_LENGTH = 20
  private val STROKE = new BasicStroke(4.0f)
  private val COLOR = new Color(120, 40, 40)
  private val FILL_COLOR = new Color(190, 180, 140)
}

class CounterWeight(var lever: Lever, var mass: Double) extends RenderablePart {
  def getMass: Double = mass

  def setMass(mass: Double): Unit = {
    this.mass = mass
  }

  override def render(g2: Graphics2D, scale: Double): Unit = {
    g2.setStroke(CounterWeight.STROKE)
    g2.setColor(CounterWeight.COLOR)
    val cwLeverLength = lever.getCounterWeightLeverLength
    val cos = SCALE_FACTOR * cwLeverLength * Math.cos(angle)
    val sin = SCALE_FACTOR * cwLeverLength * Math.sin(angle)
    val attachPt = new Vector2d(STRUT_BASE_X + sin, (-SCALE_FACTOR * height).toInt - cos)
    g2.drawLine((scale * attachPt.x).toInt, (BASE_Y + scale * attachPt.y).toInt, (scale * attachPt.x).toInt, (BASE_Y + scale * (attachPt.y + CounterWeight.WEIGHT_HANG_LENGTH)).toInt)
    val radius = (SCALE_FACTOR * 0.05 * Math.cbrt(mass)).toInt
    g2.setColor(CounterWeight.COLOR)
    val diameter = (scale * 2.0 * radius).toInt
    val xOval = (scale * (attachPt.x - radius)).toInt
    val yOval = (BASE_Y + scale * (attachPt.y + CounterWeight.WEIGHT_HANG_LENGTH)).toInt
    g2.drawOval(xOval, yOval, diameter, diameter)
    g2.setColor(CounterWeight.FILL_COLOR)
    g2.fillOval(xOval, yOval, diameter, diameter)
    val bottomY = attachPt.y + CounterWeight.WEIGHT_HANG_LENGTH + diameter
    if (showVelocityVectors) {
      g2.setStroke(VELOCITY_VECTOR_STROKE)
      g2.setColor(VELOCITY_VECTOR_COLOR)
      val velocityMagnitude = lever.getCounterWeightLeverLength * angularVelocity * Math.sin(angle)
      g2.drawLine((scale * attachPt.x).toInt, (scale * bottomY + BASE_Y).toInt,
        (scale * attachPt.x).toInt, (scale * (bottomY + velocityMagnitude) + BASE_Y).toInt)
    }
    if (showForceVectors) {
      g2.setStroke(FORCE_VECTOR_STROKE)
      g2.setColor(FORCE_VECTOR_COLOR)
      g2.drawLine((scale * attachPt.x).toInt,
        (scale * bottomY).toInt + BASE_Y, (scale * attachPt.x).toInt,
        (scale * (bottomY + PhysicsConstants.GRAVITY * getMass)).toInt + BASE_Y)
    }
  }
}
