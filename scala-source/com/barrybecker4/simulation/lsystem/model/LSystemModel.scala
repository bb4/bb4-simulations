// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model

import com.barrybecker4.simulation.lsystem.rendering.LSystemRenderer
import javax.swing.JOptionPane
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
  val DEFAULT_ITERATIONS = 1
  val DEFAULT_ANGLE = 90.0
  val DEFAULT_SCALE = 0.9
  val DEFAULT_SCALE_FACTOR = 0.7
  private val DEFAULT_SIZE = 256
}

class LSystemModel() extends Panable {

  private var renderer: LSystemRenderer = _
  private var numIterations = 0
  private var angle = .0
  private var scale = .0
  private var scaleFactor = .0
  private var expression: String = _
  private var renderRequested = false
  reset()

  def setSize(width: Int, height: Int): Unit = {
    if (width != renderer.getWidth || height != renderer.getHeight) requestRender(width, height)
  }

  def incrementOffset(incrementAmount: Location) {
    renderer.incrementOffset(incrementAmount)
  }

  def reset(): Unit = {
    numIterations = DEFAULT_ITERATIONS
    angle = DEFAULT_ANGLE
    scale = DEFAULT_SCALE
    scaleFactor = DEFAULT_SCALE_FACTOR
    expression = DEFAULT_EXPRESSION
    renderer = new LSystemRenderer(DEFAULT_SIZE, DEFAULT_SIZE, expression, numIterations, angle, scale, scaleFactor)
  }

  def setNumIterations(num: Int) {
    if (num != this.numIterations) {
      numIterations = num
      requestRender(renderer.getWidth, renderer.getHeight)
    }
  }

  def setAngle(ang: Double) {
    if (ang != angle) {
      angle = ang
      requestRender(renderer.getWidth, renderer.getHeight)
    }
  }

  def setScale(value: Double) {
    if (value != scale) {
      scale = value
      requestRender(renderer.getWidth, renderer.getHeight)
    }
  }

  def setScaleFactor(value: Double) {
    if (value != scaleFactor) {
      scaleFactor = value
      requestRender(renderer.getWidth, renderer.getHeight)
    }
  }

  def setExpression(exp: String) {
    if (!(exp == expression)) {
      expression = exp
      requestRender(renderer.getWidth, renderer.getHeight)
    }
  }

  def getExpression: String = {renderer.getSerializedExpression }

  private def requestRender(width: Int, height: Int) {
    try {
      renderer = new LSystemRenderer(width, height, expression, numIterations, angle, scale, scaleFactor)
      renderRequested = true
    } catch {
      case e: IllegalArgumentException =>
        JOptionPane.showMessageDialog(null, e.getMessage)
    }
  }

  def getImage: BufferedImage = renderer.getImage

  /**
    * @param timeStep number of rows to compute on this timestep.
    * @return true when done computing whole renderer.
    */
  def timeStep(timeStep: Double): Boolean = {
    if (renderRequested) {
      renderRequested = false
      //Profiler.getInstance.startCalculationTime()
      renderer.render()
    }
    false
  }
}
