// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

import java.awt._
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.HEIGHT


/**
  * A physical piece of an object.
  * @author Barry Becker
  */
object RenderablePart {
  val BASE_Y = 450
  val SCALE_FACTOR = 70
  val BASE_X = 80
  val STRUT_BASE_X = 300
  val VELOCITY_VECTOR_STROKE = new BasicStroke(1.0f)
  val FORCE_VECTOR_STROKE = new BasicStroke(0.3f)
  val VELOCITY_VECTOR_COLOR = new Color(70, 10, 255, 200)
  val FORCE_VECTOR_COLOR = new Color(200, 0, 80, 200)
  var height: Double = HEIGHT
  var angle = .0
  var angularVelocity: Double = 0
  var showVelocityVectors = true
  var showForceVectors = true

  def getHieght: Double = height

  def setHeight(height: Double): Unit = {
    RenderablePart.height = height
  }

  def getShowVelocityVectors: Boolean = showVelocityVectors

  def setShowVelocityVectors(showVelocityVectors: Boolean): Unit = {
    RenderablePart.showVelocityVectors = showVelocityVectors
  }

  def getShowForceVectors: Boolean = showForceVectors

  def setShowForceVectors(showForceVectors: Boolean): Unit = {
    RenderablePart.showForceVectors = showForceVectors
  }

  def getAngle: Double = angle

  def setAngle(angle: Double): Unit = {
    RenderablePart.angle = angle
  }

  def getAngularVelocity: Double = angularVelocity

  def setAngularVelocity(angularVelocity: Double): Unit = {
    this.angularVelocity = angularVelocity
  }
}

abstract class RenderablePart() {
  def render(g2: Graphics2D, scale: Double): Unit
}
