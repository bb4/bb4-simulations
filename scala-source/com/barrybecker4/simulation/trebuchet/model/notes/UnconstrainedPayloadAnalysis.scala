// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.notes

import AnalysisConstants.*
import javax.vecmath.Vector2d

object UnconstrainedPayloadAnalysis {

    def main(args: Array[String]): Unit = {
        val a = UnconstrainedPayloadAnalysis()
        println("Moment of inertia = " + a.getLeverArmMomentOfInertia)
    }

}

/**
  * The goal is to find the position of the payload.
  * D this by finding the acceleration of the payload
  */
class UnconstrainedPayloadAnalysis {

    private var theta: Double = 0 // Angle between the vertical and the beam, on the counterweight side. Negative
    private var alpha: Double = 0 // Angle between the beam and the cable holding the payload. Positive
    private var beta: Double = 0 // Angle between the beam and the cable holding the counterweight. Positive


    def getLeverArmMomentOfInertia: Double = {
        1/12.0 * Math.pow(LEVER_MASS * (L1 + L2), 2.0) + Math.pow(LEVER_MASS * (L2 - s), 2.0)
    }

    def getProjectilePosition: Vector2d = {
        val x = L2 * Math.sin(theta) + L3 * Math.sin(alpha - theta)
        val y = -L2 * Math.cos(theta) + L3 * Math.cos(alpha - theta)
        new Vector2d(x, y)
    }

    def getProjectileAcceleration: Vector2d = {
        val T = getCounterweightCableTension
        val ax = -T * Math.sin(alpha - theta) / PROJECTILE_MASS
        val ay = (-T * Math.cos(alpha - theta) - COUNTERWEIGHT_MASS) / PROJECTILE_MASS
        Vector2d(ax, ay)
    }

    // m * (g +/- a) - where do we get this? from getCounter
    private def getCounterweightCableTension: Double = {
        10
    }



}
