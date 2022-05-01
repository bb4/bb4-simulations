// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.simulation.common.PhysicsConstants
import com.barrybecker4.simulation.trebuchet.model.Variables
import com.barrybecker4.simulation.trebuchet.model.parts.CounterWeight.WEIGHT_HANG_LENGTH
import com.barrybecker4.simulation.trebuchet.model.parts.{CounterWeight, Lever}
import com.barrybecker4.simulation.trebuchet.rendering.RenderingConstants

import java.awt.*
import javax.vecmath.Vector2d


/**
  * @author Barry Becker Date: Sep 25, 2005
  */
object CounterWeight {
  private val WEIGHT_HANG_LENGTH = 20
}

class CounterWeight(var lever: Lever, var mass: Double, variables: Variables) {

  def getMass: Double = mass

  def setMass(mass: Double): Unit = {
    this.mass = mass
  }

  def getAngle: Double = variables.angle
  def getAngularVelocity: Double = variables.angularVelocity
  def getWeightHangLength: Int = WEIGHT_HANG_LENGTH
}
