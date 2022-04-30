// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.simulation.trebuchet.model.parts.{Lever, RenderablePart}
import com.barrybecker4.simulation.trebuchet.model.parts.RenderablePart.*
import java.awt.*
import javax.vecmath.Vector2d


/**
  * @author Barry Becker Date: Sep 25, 2005
  */
object Lever { // amount of mass in kg per meter magnitude of the lever
  private val LEVER_MASS_PER_METER = 2.0
  private val LEVER_STROKE = new BasicStroke(10.0f)
  private val LEVER_COLOR = new Color(80, 60, 180)
}

/**
  * The angle of the level wrt horizontal (0 being horizontal)
  * @param counterWeightLeverLength
  * @param slingLeverLength
  */
class Lever(var counterWeightLeverLength: Double, var slingLeverLength: Double) extends RenderablePart {
  private[model] def getSlingLeverLength = slingLeverLength

  private[model] def setSlingLeverLength(slingLeverLength: Double): Unit = {
    this.slingLeverLength = slingLeverLength
  }

  private[model] def getCounterWeightLeverLength = counterWeightLeverLength

  private[model] def setCounterWeightLeverLength(counterWeightLeverLength: Double): Unit = {
    this.counterWeightLeverLength = counterWeightLeverLength
  }

  /**
    * @return the mass in kilograms
    */
  def getMass: Double = Lever.LEVER_MASS_PER_METER * getTotalLength

  private def getTotalLength = counterWeightLeverLength + slingLeverLength

  // @@ make constant to improve perf?
  private[model] def getFulcrumPosition =
    new Vector2d(STRUT_BASE_X, (-SCALE_FACTOR * height).toInt)

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

  override def render(g2: Graphics2D, scale: Double, height: Int): Unit = {
    g2.setStroke(Lever.LEVER_STROKE)
    g2.setColor(Lever.LEVER_COLOR)

    val y = height - BASE_Y
    val fulcrumPos = getFulcrumPosition
    val cos = SCALE_FACTOR * Math.cos(angle)
    val sin = SCALE_FACTOR * Math.sin(angle)

    g2.drawLine((scale * (fulcrumPos.x + sin * counterWeightLeverLength)).toInt,
      (scale * (fulcrumPos.y - cos * counterWeightLeverLength) + y).toInt,
      (scale * (fulcrumPos.x - sin * slingLeverLength)).toInt,
      (scale * (fulcrumPos.y + cos * slingLeverLength)).toInt + y)
  }
}
