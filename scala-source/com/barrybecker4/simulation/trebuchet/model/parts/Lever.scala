// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.{DEFAULT_CW_LEVER_LENGTH, DEFAULT_SLING_LEVER_LENGTH, HEIGHT, SCALE_FACTOR}
import com.barrybecker4.simulation.trebuchet.model.parts.Lever

import java.awt.*
import javax.vecmath.Vector2d


/**
  * @author Barry Becker Date: Sep 25, 2005
  */
object Lever {
  // amount of mass in kg per meter magnitude of the lever
  private val LEVER_MASS_PER_METER = 2.0
}

/**
  * The angle of the level wrt horizontal (0 being horizontal)
  */
class Lever(base: Base, var counterWeightLeverLength: Double, var slingLeverLength: Double) {

  private var angle: Double = 0.0
  private var angularVelocity: Double = 0.0
  
  def getSlingLeverLength:Double = slingLeverLength
  def setSlingLeverLength(slingLeverLength: Double): Unit = {
    this.slingLeverLength = slingLeverLength
  }

  def getCounterWeightLeverLength: Double = counterWeightLeverLength
  def setCounterWeightLeverLength(counterWeightLeverLength: Double): Unit = {
    this.counterWeightLeverLength = counterWeightLeverLength
  }

  /**
    * @return the mass in kilograms
    */
  def getMass: Double = Lever.LEVER_MASS_PER_METER * getTotalLength
  def getStrutBaseX: Double = base.getStrutBaseX
  def getBaseY: Int = base.getBaseY
  def getAngle: Double = angle
  def setAngle(angle: Double): Unit = {
      this.angle = angle
  }
  def getAngularVelocity: Double = angularVelocity
  def setAngularVelocity(angularVelocity: Double): Unit = {
    this.angularVelocity = angularVelocity
  }

  private def getTotalLength = counterWeightLeverLength + slingLeverLength

  // @@ make constant to improve perf?
  def getFulcrumPosition =
    new Vector2d(base.getStrutBaseX, (-SCALE_FACTOR * HEIGHT).toInt)

  /**
    * I = 1/3 * ML squared
    * see physics book.
    * @return the moment of inertia for the lever
    */
  private[model] def getInertia = {
    val sllSquared = slingLeverLength * slingLeverLength
    val cwlSquared = counterWeightLeverLength * counterWeightLeverLength
    getMass / 3.0 * (sllSquared + cwlSquared)
  }

}
