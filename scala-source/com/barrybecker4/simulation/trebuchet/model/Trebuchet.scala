// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

import com.barrybecker4.ui.util.Log
import javax.vecmath.Vector2d
import java.awt.Color
import java.awt.Graphics2D
import com.barrybecker4.simulation.common.PhysicsConstants.GRAVITY
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants._
import java.lang.Math.PI
import java.lang.Math.asin
import java.lang.Math.cos
import java.lang.Math.sin
import com.barrybecker4.simulation.trebuchet.model._


/**
  * Data structure and methods for representing a single dynamic trebuchet (advanced form of a catapult)
  * The geometry of the trebuchet is defined by constants in TebuchetConstants.
  *
  * Performance Improvements:
  *    - profile (where is the time spent? rendering or computation)
  *
  * @author Barry Becker
  */
object Trebuchet {
  protected val MIN_EDGE_ANGLE = 0.3
  private val GRAVITY_VEC = new Vector2d(0, GRAVITY)
  private val MAX_LEVER_ANGLE = PI - 0.1
  private val NUM_PARTS = 5
  // the time since the start of the simulation
  private var logger = new Log
}

/**
  * Use a hard-coded static data interface to initialize
  * so it can be easily run in an applet without using resources.
  */
class Trebuchet() {

  private var lever: Lever = _
  private var counterWeight: CounterWeight = _
  private var sling: Sling = _
  private var projectile: Projectile = _
  private val forceFromHook = new Vector2d(0, 0)
  private var part = new Array[RenderablePart](Trebuchet.NUM_PARTS)
  // tweakable rendering parameters
  private var showVelocityVectors = false
  private var showForceVectors = false
  // scales the geometry of the trebuchet
  private var scale = SCALE
  commonInit()

  def reset(): Unit = {
    RenderablePart.setAngularVelocity(0)
    commonInit()
  }

  private def commonInit(): Unit = {
    val angle = PI / 2.0 - asin(HEIGHT / DEFAULT_SLING_LEVER_LENGTH)
    RenderablePart.setAngle(angle)
    val base = new Base
    part(0) = base
    lever = new Lever(DEFAULT_CW_LEVER_LENGTH, DEFAULT_SLING_LEVER_LENGTH)
    part(1) = lever
    counterWeight = new CounterWeight(lever, DEFAULT_COUNTER_WEIGHT_MASS)
    part(2) = counterWeight
    projectile = new Projectile(DEFAULT_PROJECTILE_MASS)
    sling = new Sling(DEFAULT_SLING_LENGTH, DEFAULT_SLING_RELEASE_ANGLE, lever, projectile)
    part(3) = sling
    part(4) = projectile
  }

  /**
    * steps the simulation forward in time
    * if the timestep is too big inaccuracy and instability may result.
    * @return the new timestep
    */
  def stepForward(timeStep: Double): Double = {
    //logger_.println(1, LOG_LEVEL, "stepForward: about to update (timeStep="+timeStep+')');
    var angle = RenderablePart.getAngle
    var angularVelocity = RenderablePart.getAngularVelocity
    val slingAngle = sling.getAngleWithLever
    val torque = calculateTorque(angle, slingAngle)
    val inertia = calculateInertia
    var angularAcceleration: Double = 0
    if (angle < Trebuchet.MAX_LEVER_ANGLE) {
      angularAcceleration = inertia / torque // in radians per second squared

      angularVelocity += timeStep * angularAcceleration
      angle += timeStep * angularVelocity
    }
    else {
      angularVelocity = 0
      angle = Trebuchet.MAX_LEVER_ANGLE
    }
    RenderablePart.setAngle(angle)
    RenderablePart.setAngularVelocity(angularVelocity)
    //System.out.println("angle="+angle+"  angularVelocity_="
    //  +angularVelocity +" angularAcceleration="+angularAcceleration);
    // calculate the forces acting on the projectile.
    // the magnitude of the tangential force at the hook
    if (!projectile.isReleased) {
      val tangentialForceAtHook = torque / lever.getSlingLeverLength
      //System.out.println("tangentialForceAtHook="+tangentialForceAtHook);
      val slingAngleWithHorz = sling.getAngleWithHorz
      forceFromHook.set(-cos(slingAngleWithHorz), sin(slingAngleWithHorz)) //sin(PI - angle), -cos(PI + angle));

      forceFromHook.scale(tangentialForceAtHook * sin(slingAngle))
      val gravityForce = new Vector2d(Trebuchet.GRAVITY_VEC)
      gravityForce.scale(projectile.getMass)
      forceFromHook.add(gravityForce)
      // also add a restoring force which is proportional to the distnace from the attachpoint on the sling
      // if we have not yet been released.
      val restoreForce = sling.getProjectileAttachPoint
      restoreForce.sub(projectile.getPosition)
      restoreForce.scale(100.0)
      forceFromHook.add(restoreForce)
      projectile.setForce(forceFromHook, timeStep)
    }
    else {
      val gravityForce = new Vector2d(Trebuchet.GRAVITY_VEC)
      gravityForce.scale(projectile.getMass)
      projectile.setForce(gravityForce, timeStep)
    }
    // at the time when it is released, the only force acting on it will be gravity.
    if (!projectile.isReleased && slingAngle >= (PI + sling.getReleaseAngle)) {
      println("##########################################################################")
      println("released!  slingAngle = " + slingAngle + " sling release angle = " + sling.getReleaseAngle)
      println("##########################################################################")
      projectile.setReleased(true)
    }
    timeStep
  }

  private def calculateTorque(angle: Double, slingAngle: Double) = {
    var primaryTorque: Double = 0
    if (angle < Trebuchet.MAX_LEVER_ANGLE)
      primaryTorque = lever.getCounterWeightLeverLength * sin(angle) * GRAVITY * counterWeight.getMass
    var dragTorque: Double = 0
    if (projectile.isOnRamp) { // case when the projectile is still on the ramp
      dragTorque = -lever.getSlingLeverLength * projectile.getMass * GRAVITY * RAMP_FRICTION * sin(slingAngle)
    }
    else { // case when the projectile is no longer on the ramp
      val r = projectile.getDistanceFrom(lever.getFulcrumPosition)
      dragTorque = r * projectile.getMass * GRAVITY * cos(PI - angle - slingAngle) * sin(angle)
    }
    //println("torque= primaryTorque("+primaryTorque+")" +
    //        " + dragTorque("+dragTorque+")= "+(primaryTorque+dragTorque));
    primaryTorque + dragTorque
  }

  /**
    * Got this from a physics text
    *
    * I = LEVER_MASS / 3  (b^3 + c^3) + projectileMass/3 * r squared
    * @return the calculated interia
    */
  private def calculateInertia = lever.getInertia + projectile.getInertia(lever.getFulcrumPosition)

  def setScale(scale: Double): Unit = {
    this.scale = scale
  }

  def getScale: Double = scale

  def setShowVelocityVectors(show: Boolean): Unit = {
    showVelocityVectors = show
  }

  def getShowVelocityVectors: Boolean = showVelocityVectors

  def setShowForceVectors(show: Boolean): Unit = {
    showForceVectors = show
  }

  def getShowForceVectors: Boolean = showForceVectors

  def getCounterWeightLeverLength: Double = lever.getCounterWeightLeverLength

  def setCounterWeightLeverLength(counterWeightLeverLength: Double): Unit = {
    lever.setCounterWeightLeverLength(counterWeightLeverLength)
  }

  def getSlingLeverLength: Double = lever.getSlingLeverLength

  def setSlingLeverLength(slingLeverLength: Double): Unit = {
    lever.setSlingLeverLength(slingLeverLength)
  }

  def getCounterWeightMass: Double = counterWeight.getMass

  def setCounterWeightMass(counterWeightMass: Double): Unit = {
    this.counterWeight.setMass(counterWeightMass)
  }

  def getSlingLength: Double = sling.getLength

  def setSlingLength(slingLength: Double): Unit = {
    this.sling.setLength(slingLength)
  }

  def getProjectileMass: Double = projectile.getMass

  def setProjectileMass(projectileMass: Double): Unit = {
    this.projectile.setMass(projectileMass)
  }

  def getSlingReleaseAngle: Double = sling.getReleaseAngle

  def setSlingReleaseAngle(slingReleaseAngle: Double): Unit = {
    this.sling.setReleaseAngle(slingReleaseAngle)
  }

  /**
    * Render the Environment on the screen
    */
  def render(g: Graphics2D): Unit = {

    g.setColor(Color.black) // default

    // render each part
    for (i <- 0 until Trebuchet.NUM_PARTS) {
      if (part(i) != null) part(i).render(g, getScale)
    }
  }
}
