// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.notes.math

import com.barrybecker4.simulation.trebuchet.model.notes.math.AnalysisConstants.*

import javax.vecmath.Vector2d
import Math.{sin, cos, pow, PI, atan, asin}

object PayloadAnalysis {

    def main(args: Array[String]): Unit = {
        PayloadAnalysis().runSimulation()
    }

}

/**
  * The goal is to find the position of the payload.
  * Do this by finding the acceleration of the payload.
  * Refer to https://www.real-world-physics-problems.com/trebuchet-physics.html
  * The origin (0, 0) of the whole system is at point P the fulcrum point of the lever arm
  */
class PayloadAnalysis {

    // Angle between the vertical and the beam, on the counterweight side. Negative
    private var theta: Double = INITIAL_THETA
    private var alpha: Double = calculateAlpha(INITIAL_THETA) // Angle between the beam and the cable holding the payload. Positive

    private var projectileAcceleration = Vector2d(0, 0)
    private var projectileVelocity = Vector2d(0, 0)
    private var projectilePosition = getInitialProjectilePosition(INITIAL_THETA)


    def runSimulation(): Unit = {
        var time = 0.0
        var thetaVelocity = 0.0

        while (!isProjectileLanded && time < 4.0) {
            time += TIME_STEP
            thetaVelocity += TIME_STEP * getThetaAcceleration
            theta += TIME_STEP * thetaVelocity
            alpha = calculateAlpha(theta)

            // find the acceleration, then velocity, then position of the projectile
            projectileAcceleration = getProjectileAcceleration
            projectileVelocity.add(Vector2d(projectileAcceleration.x * TIME_STEP, projectileAcceleration.y * TIME_STEP))
            projectilePosition = getProjectilePosition
            println("projectile position = " + projectilePosition)
        }
        println("Done!")
    }

    // the clockwise angular acceleration of the beam
    def getThetaAcceleration: Double = {
        getSumOfMomentsAboutPivot / getLeverArmMomentOfInertia
    }

    def getSumOfMomentsAboutPivot: Double = {
        getProjectileCableTension * sin(alpha) * L2 -
          LEVER_MASS * GRAVITY * sin(theta) * (L2 - s) -
          getCounterweightCableTension * sin(-theta) * L1
    }

    /**
      * One thing that seems to be missing from the linked reference is how to find T from T2.
      * I believe T = -sin(theta) sin(alpha) T2
      */
    def getProjectileCableTension: Double = -sin(theta) * sin(alpha) * getCounterweightCableTension

    /**
      * Tension on the counterweight cable (T2)
      * m * (g +/- a) - where do we get this? from getCounter
      */
    def getCounterweightCableTension: Double = COUNTERWEIGHT_MASS * GRAVITY

    // Ip = 1/12 Mb(L1 + L2)^2 + Mb(L2 - s)^2
    def getLeverArmMomentOfInertia: Double = {
        1/12.0 * LEVER_MASS * pow(L1 + L2, 2.0) + LEVER_MASS * pow(L2 - s, 2.0)
    }

    def getProjectileAcceleration: Vector2d = {
        if (isOnChute) {
            // there will only be an x component of acceleration while on the chute
            Vector2d(getProjectileCableTension * sin(alpha - theta) / PROJECTILE_MASS, 0)
        }
        else if (!isReleased) {
            val T = getCounterweightCableTension
            val ax = -T * sin(alpha - theta) / PROJECTILE_MASS
            val ay = (-T * cos(alpha - theta) - COUNTERWEIGHT_MASS) / PROJECTILE_MASS
            Vector2d(ax, ay)
        }
        else {  // the sling has been released
            Vector2d(0, -GRAVITY)
        }
    }

    /**
      * There are 3 different ways to compute the projectile position depending on which phase we are in
      * phase 1: The projectile is in the guide chute
      * phase 2: The projectile is no longer in the chute, but is being pulled by the sling
      * phase 3: The projectile is free of the sling, and following a trajectory determined by the final force
      * @return
      */
    def getProjectilePosition: Vector2d = {
        val newPosition = Vector2d(projectilePosition.x + TIME_STEP * projectileVelocity.x,
                                   projectilePosition.y + TIME_STEP * projectileVelocity.y)
        newPosition
    }

    private def getInitialProjectilePosition(theta: Double) = {
        val initialAlpha = calculateAlpha(theta)
        val x = L2 * sin(theta) + L3 * sin(alpha - theta)
        val y = -L2 * cos(theta) + L3 * cos(alpha - theta)
        new Vector2d(x, y)
    }

    // alpha - PI/2 - atan( -1 / staticFriction )
    private def getDelta: Double =
        alpha - PI / 2.0 - atan( -1.0 / STATIC_FRICTION)

    private def calculateAlpha(theta: Double): Double =
        PI / 2.0 + theta + asin(getD1 / L3)

    private def getD1: Double =
        L2 * cos(INITIAL_THETA) - L2 * cos(theta)

    // Lifts off the chute when the vertical component of the tension exceeds mg.
    private def isOnChute: Boolean = {
        val verticalTension = getProjectileCableTension * cos(alpha - theta)
        verticalTension > 0
    }

    private def isReleased: Boolean = theta < SLING_RELEASE_ANGLE
    private def isProjectileLanded: Boolean = projectilePosition.y >= cos(calculateAlpha(INITIAL_THETA)) * L2

}
