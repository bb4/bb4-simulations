// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.math.linear.LinearUtil
import com.barrybecker4.simulation.trebuchet.model.parts.Projectile
import java.awt.*
import javax.vecmath.Vector2d


class Projectile(base: Base, val projectileMass: Double) {
  var mass: Double = projectileMass
  private val radius = 0.35 * Math.cbrt(mass)
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
  def getForce: Vector2d = force
  def getRadius: Double = radius

  def setY(y: Double): Unit = {
    position.y = y
  }

  def getY: Double = position.y

  def setPosition(position: Vector2d): Unit = {
    this.position = position
  }

  def getMass: Double = mass
  def getDistanceFrom(referencePoint: Vector2d): Double = LinearUtil.distance(referencePoint, position)
  def getBaseY: Double = base.getBaseY

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

    val newXPos = position.x + timeStep * velocity.x
    val newYPos = position.y + timeStep * velocity.y
    //println("x = " + position.x + " y = " + position.y)
    setPosition(new Vector2d(newXPos, newYPos))
    //println("isOnRamp " + isOnRamp + "  comparing pos.y = " + position.y + " and rampHt = " + (-base.getRampHeight))
    if (isOnRamp && position.y < -base.getRampHeight) {
      isOnRamp = false
      println("*********** no longer on ramp!")
    }
  }

  def getVelocity: Vector2d = velocity
}
