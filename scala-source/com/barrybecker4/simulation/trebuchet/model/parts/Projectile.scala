// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.math.linear.LinearUtil
import com.barrybecker4.simulation.trebuchet.model.parts.Projectile.TRAIL_COLOR
import com.barrybecker4.simulation.trebuchet.model.parts.{Projectile, RenderablePart}
import com.barrybecker4.simulation.trebuchet.model.parts.RenderablePart.*

import java.awt.*
import javax.vecmath.Vector2d


/**
  * @author Barry Becker Date: Sep 25, 2005
  */
object Projectile {
  private val LEVER_STROKE = new BasicStroke(10.0f)
  private val BORDER_COLOR = new Color(140, 50, 110)
  private val FILL_COLOR = new Color(80, 150, 10)
  private val TRAIL_COLOR = new Color(100, 0, 0, 190)
}

class Projectile(val projectileMass: Double) extends RenderablePart {
  var mass: Double = projectileMass
  private var radius = 0.05 * Math.cbrt(mass)
  private var position = new Vector2d
  var isOnRamp: Boolean = true
  var isReleased: Boolean = false
  private val acceleration = new Vector2d(0, 0)
  private val velocity = new Vector2d(0, 0)
  private val force = new Vector2d(0, 0)
  private var pastPositions = Seq[IntLocation]()


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

  def getMass: Double = mass
  def getRadius: Double = radius
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

    setPosition(new Vector2d(position.x + SCALE_FACTOR * timeStep * velocity.x, position.y + SCALE_FACTOR * timeStep * velocity.y))
    if (isOnRamp && position.y < -4) {
      isOnRamp = false
      println("*********** no longer on ramp!")
    }
  }

  def getVelocity: Vector2d = velocity

  override def render(g2: Graphics2D, scale: Double, height: Int): Unit = {

    val location = getOvalLocation(height, scale)

    val radius = (SCALE_FACTOR * this.radius).toInt
    val diameter = (scale * 2.0 * radius).toInt
    g2.setColor(Projectile.BORDER_COLOR)

    g2.drawOval(location.getX, location.getY, diameter, diameter)
    g2.setColor(Projectile.FILL_COLOR)
    g2.fillOval(location.getX, location.getY, diameter, diameter)

    if (isReleased) {
      // show a little larger once released
      val d = (diameter + scale * 4.0).toInt
      g2.drawOval(location.getX, location.getY, d, d)

      pastPositions :+= location
      drawTrail(g2, diameter)
    }

    val y = height - BASE_Y
    if (showVelocityVectors) {
      g2.setStroke(VELOCITY_VECTOR_STROKE)
      g2.setColor(VELOCITY_VECTOR_COLOR)
      g2.drawLine((scale * position.x).toInt, (y + scale * position.y).toInt,
        (scale * (position.x + velocity.x)).toInt, (y + scale * (position.y + velocity.y)).toInt)
    }
    if (showForceVectors) {
      g2.setStroke(FORCE_VECTOR_STROKE)
      g2.setColor(FORCE_VECTOR_COLOR)
      g2.drawLine((scale * position.x).toInt, (y + scale * position.y).toInt,
        (scale * (position.x + force.x)).toInt, (y + scale * (position.y + force.y)).toInt)
    }
  }

  private def drawTrail(g2: Graphics2D, diameter: Int): Unit = {
    g2.setColor(TRAIL_COLOR)
    pastPositions.foreach(position => {
      g2.drawOval(position.getX, position.getY, diameter, diameter)
    })
  }

  private def getOvalLocation(height: Int, scale: Double): IntLocation = {
    val y = height - BASE_Y
    new IntLocation((scale * (position.y - radius) + y).toInt, (scale * (position.x - radius)).toInt)
  }
}
