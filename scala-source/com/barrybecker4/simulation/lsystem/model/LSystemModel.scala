// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.lsystem.rendering.LSystemRenderer
import javax.swing.JOptionPane
import java.awt.image.BufferedImage
import LSystemModel._


/**
  * See https://en.wikipedia.org/wiki/L-system
  * For explanation of the grammar and different types of L-systems.
  * The language should be expanded to include support for more terms.
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

class LSystemModel() {
  reset()
  private var renderer: LSystemRenderer = _
  private var numIterations = 0
  private var angle = .0
  private var scale = .0
  private var scaleFactor = .0
  private var expression: String = _
  private var restartRequested = false

  def setSize(width: Int, height: Int): Unit = {
    if (width != renderer.getWidth || height != renderer.getHeight) requestRestart(width, height)
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
      requestRestart(renderer.getWidth, renderer.getHeight)
    }
  }

  def setAngle(ang: Double) {
    if (ang != angle) {
      angle = ang
      requestRestart(renderer.getWidth, renderer.getHeight)
    }
  }

  def setScale(value: Double) {
    if (value != scale) {
      scale = value
      requestRestart(renderer.getWidth, renderer.getHeight)
    }
  }

  def setScaleFactor(value: Double) {
    if (value != scaleFactor) {
      scaleFactor = value
      requestRestart(renderer.getWidth, renderer.getHeight)
    }
  }

  def setExpression(exp: String) {
    if (!(exp == expression)) {
      expression = exp
      requestRestart(renderer.getWidth, renderer.getHeight)
    }
  }

  private def requestRestart(width: Int, height: Int) {
    try {
      renderer = new LSystemRenderer(width, height, expression, numIterations, angle, scale, scaleFactor)
      restartRequested = true
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
    if (restartRequested) {
      restartRequested = false
      renderer.reset()
      Profiler.getInstance.startCalculationTime()
      renderer.render()
    }
    false
  }
}
