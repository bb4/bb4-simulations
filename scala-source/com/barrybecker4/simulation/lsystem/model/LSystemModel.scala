// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model

import com.barrybecker4.simulation.lsystem.rendering.LSystemRenderer
import java.awt.image.BufferedImage

import LSystemModel._
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.lsystem.Panable


/**
  * See https://en.wikipedia.org/wiki/L-system
  * For explanation of the grammar and different types of L-systems.
  * The language should be expanded to include support for more terms.
  *
  * move all the private vars into renderer.
  * @author Barry Becker
  */
object LSystemModel {
  val DEFAULT_EXPRESSION = "F(+F)F(-F)F"
  val KOCH_SNOWFLAKE = "F+F--F+F"
  val DEFAULT_ITERATIONS = 1
  val DEFAULT_ANGLE = 90.0
  val DEFAULT_SCALE = 0.9
  val DEFAULT_SCALE_FACTOR = 0.7
  private val DEFAULT_SIZE = 256
}

class LSystemModel() extends Panable {
  private var renderer: LSystemRenderer = _
  reset()

  def reset() {
    renderer = new LSystemRenderer(DEFAULT_SIZE, DEFAULT_SIZE,
      DEFAULT_EXPRESSION, DEFAULT_ITERATIONS, DEFAULT_ANGLE, DEFAULT_SCALE, DEFAULT_SCALE_FACTOR)
    renderer.render()
  }

  def setSize(width: Int, height: Int) {
    if (width != renderer.getWidth || height != renderer.getHeight) renderer.setDimensions(width, height)
  }

  def incrementOffset(incrementAmount: Location) { renderer.incrementOffset(incrementAmount) }
  def setNumIterations(num: Int) { renderer.setNumIterations(num) }
  def setAngle(ang: Double) { renderer.setAngleInc(ang) }
  def setScale(value: Double) { renderer.setScale(value)}
  def setScaleFactor(value: Double) { renderer.setScaleFactor(value) }
  def setExpression(exp: String) { renderer.setExpression(exp) }
  def getExpression: String = {renderer.getSerializedExpression }
  def getImage: BufferedImage = renderer.getImage

  /**
    * @param timeStep number of rows to compute on this timestep.
    * @return true when done computing whole renderer.
    */
  def timeStep(timeStep: Double) {
    renderer.render()
  }
}
