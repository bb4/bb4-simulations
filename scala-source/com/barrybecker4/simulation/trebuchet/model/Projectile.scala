// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

import com.barrybecker4.common.math.LinearUtil
import javax.vecmath.Vector2d
import java.awt._
import RenderablePart._


/**
  * @author Barry Becker Date: Sep 25, 2005
  */
object Projectile {
  private val LEVER_STROKE = new BasicStroke(10.0f)
  private val BORDER_COLOR = new Color(140, 50, 110)
  private val FILL_COLOR = new Color(80, 150, 10)
}

class Projectile(val projectileMass: Double) extends RenderablePart {
  private var mass = projectileMass
  private var radius = 0.05 * Math.cbrt(mass)
  private var position = new Vector2d
  var isOnRamp: Boolean = true
  var isReleased: Boolean = false
  private val acceleration = new Vector2d(0, 0)
  private val velocity = new Vector2d(0, 0)
  private val force = new Vector2d(0, 0)

  def setX(x: Double): Unit = {
    position.x = x
  }

  def getX: Double = position.x

  def getPosition: Vector2d = position

  def setY(y: Double): Unit = {
    position.y = y
  }

  def getY: Double = position.y

  def setPosition(position: Vector2d): Unit = {
    this.position = position
  }

  def setMass(mass: Double): Unit = {
    this.mass = mass
  }

  def getMass: Double = mass
  def getRadius: Double = radius

  private def setOnRamp(onRamp: Boolean): Unit = {
    isOnRamp = onRamp
  }

  def setReleased(released: Boolean): Unit = {
    isReleased = released
  }

  def getDistanceFrom(referencePoint: Vector2d): Double = LinearUtil.distance(referencePoint, position)

  def getInertia(referencePoint: Vector2d): Double = {
    val dist = getDistanceFrom(referencePoint)
    getMass / 3.0 * dist * dist
  }

  def setForce(force: Vector2d, timeStep: Double): Unit = {
    if (isOnRamp && force.y > 0.0) force.y = 0
    this.force.set(force)
    acceleration.set(force)
    acceleration.scale(1.0 / getMass)
    val deltaVelocity = new Vector2d(acceleration)
    deltaVelocity.scale(timeStep)
    velocity.add(deltaVelocity)
    position.set(position.x + SCALE_FACTOR * timeStep * velocity.x, position.y + SCALE_FACTOR * timeStep * velocity.y)
    if (isOnRamp && position.y < (-4)) {
      setOnRamp(false)
      System.out.println("*********** no longer on ramp!")
    }
  }

  def getVelocity: Vector2d = velocity

  override def render(g2: Graphics2D, scale: Double): Unit = {
    val radius = (SCALE_FACTOR * this.radius).toInt
    g2.setColor(Projectile.BORDER_COLOR)
    val diameter = (scale * 2.0 * radius).toInt
    val ovalX = (scale * (position.x - radius)).toInt
    val ovalY = (scale * (position.y - radius) + BASE_Y).toInt
    g2.drawOval(ovalX, ovalY, diameter, diameter)
    g2.setColor(Projectile.FILL_COLOR)
    g2.fillOval(ovalX, ovalY, diameter, diameter)
    if (isReleased) {
      val d = (diameter + scale * 4.0).toInt
      g2.drawOval(ovalX, ovalY, d, d)
    }
    if (getShowVelocityVectors) {
      g2.setStroke(VELOCITY_VECTOR_STROKE)
      g2.setColor(VELOCITY_VECTOR_COLOR)
      g2.drawLine((scale * position.x).toInt, (BASE_Y + scale * position.y).toInt, (scale * (position.x + velocity.x)).toInt, (BASE_Y + scale * (position.y + velocity.y)).toInt)
    }
    if (getShowForceVectors) {
      g2.setStroke(FORCE_VECTOR_STROKE)
      g2.setColor(FORCE_VECTOR_COLOR)
      g2.drawLine((scale * position.x).toInt, (BASE_Y + scale * position.y).toInt, (scale * (position.x + force.x)).toInt, (BASE_Y + scale * (position.y + force.y)).toInt)
    }
  }
}
