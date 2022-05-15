// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

import com.barrybecker4.simulation.common.PhysicsConstants.GRAVITY
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.RAMP_FRICTION
import com.barrybecker4.simulation.trebuchet.model.TrebuchetProcessor.{GRAVITY_VEC, MAX_LEVER_ANGLE}
import com.barrybecker4.simulation.trebuchet.model.parts.{CounterWeight, Lever, Projectile, Sling}

import java.lang.Math.{PI, cos, sin}
import javax.vecmath.Vector2d


object TrebuchetProcessor {
  private val GRAVITY_VEC = new Vector2d(0, GRAVITY)
  private val MAX_LEVER_ANGLE = PI - 0.1
}

class TrebuchetProcessor(lever: Lever, counterWeight: CounterWeight, sling: Sling, projectile: Projectile) {

  /**
    * steps the simulation forward in time
    * if the timestep is too big inaccuracy and instability may result.
    * @return the new timestep
    */
  def stepForward(timeStep: Double): Double = {
    var angle = lever.getAngle
    var angularVelocity = lever.getAngularVelocity
    val slingAngle = sling.getAngleWithLever
    val torque = calculateTorque(angle, slingAngle)
    val inertia = calculateInertia
    var angularAcceleration: Double = 0

    if (angle < MAX_LEVER_ANGLE) {
      angularAcceleration = inertia / torque // in radians per second squared
      angularVelocity += timeStep * angularAcceleration
      angle += timeStep * angularVelocity
    }
    else {
      angularVelocity = 0
      angle = MAX_LEVER_ANGLE
    }
    lever.setAngle(angle)
    lever.setAngularVelocity(angularVelocity)

    projectile.setForce(calculateProjectileForce(torque), timeStep)

    // at the time when it is released, the only force acting on it will be gravity.
    if (!projectile.isReleased && slingAngle >= (PI + sling.releaseAngle)) {
      println("##########################################################################")
      println("released!  slingAngle = " + slingAngle + " sling release angle = " + sling.releaseAngle)
      println("##########################################################################")
      projectile.isReleased = true
    }
    timeStep
  }

  private def calculateProjectileForce(torque: Double): Vector2d = {
    if (!projectile.isReleased) calculateProjectileForceBeforeRelease(torque)
    else calculateProjectileForceAfterRelease()
  }

  private def calculateProjectileForceBeforeRelease(torque: Double): Vector2d = {
    // calc the magnitude of the tangential force at the hook
    val tangentialForceAtHook = torque / lever.getSlingLeverLength
    //println("tangentialForceAtHook="+tangentialForceAtHook);
    val slingAngleWithHorz = sling.getAngleWithHorz
    val forceFromHook = new Vector2d(-cos(slingAngleWithHorz), sin(slingAngleWithHorz)) //sin(PI - angle), -cos(PI + angle));

    forceFromHook.scale(tangentialForceAtHook * sin(sling.getAngleWithLever))
    val gravityForce = new Vector2d(GRAVITY_VEC)
    gravityForce.scale(projectile.getMass)
    forceFromHook.add(gravityForce)
    // also add a restoring force which is proportional to the distance from the attachPoint on the sling
    // if we have not yet been released.
    val restoreForce = sling.getProjectileAttachPoint
    restoreForce.sub(projectile.getPosition)
    restoreForce.scale(100.0)
    forceFromHook.add(restoreForce)
    forceFromHook
  }

  private def calculateProjectileForceAfterRelease(): Vector2d = {
    val gravityForce = new Vector2d(GRAVITY_VEC)
    gravityForce.scale(projectile.getMass)
    gravityForce
  }

  private def calculateTorque(angle: Double, slingAngle: Double) = {
    var primaryTorque: Double = 0
    if (angle < MAX_LEVER_ANGLE)
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
    * Got this from a physics text:
    * I = LEVER_MASS / 3  (b^3 + c^3) + projectileMass/3 * r squared
    * @return the calculated inertia
    */
  private def calculateInertia = {
    lever.getInertia + projectile.getInertia(lever.getFulcrumPosition)
  }

}
