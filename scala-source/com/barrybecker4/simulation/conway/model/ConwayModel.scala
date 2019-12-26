// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.conway.rendering.ConwayColorMap
import com.barrybecker4.simulation.conway.rendering.ConwayRenderer
import com.barrybecker4.ui.util.ColorMap
import javax.swing._
import java.awt.image.BufferedImage
import com.barrybecker4.simulation.conway.model.ConwayProcessor._
import ConwayModel._


/**
  * Communicates changing dynamic options to ConwayProcessor and controls the rendering.
  *
  * @author Barry Becker
  */
object ConwayModel {
  val DEFAULT_SCALE_FACTOR = 2
  val DEFAULT_USE_CONTINUOUS_ITERATION = true
  val DEFAULT_SHOW_SHADOWS = false
  val DEFAULT_WRAP_GRID = true
  val DEFAULT_NUM_STEPS_PER_FRAME = 1
  private val DEFAULT_WIDTH = 400
  private val DEFAULT_HEIGHT = 400
}

class ConwayModel() {
  private var processor: ConwayProcessor = _
  private var renderer: ConwayRenderer = _
  private var scale = DEFAULT_SCALE_FACTOR
  private var numStepsPerFrame = DEFAULT_NUM_STEPS_PER_FRAME
  private var useParallel = DEFAULT_USE_PARALLEL
  private var numIterations = 0
  private var restartRequested = false
  private var nextStepRequested = false
  private var continuousIteration = DEFAULT_USE_CONTINUOUS_ITERATION
  private var showShadows = DEFAULT_SHOW_SHADOWS
  private var wrapGrid = DEFAULT_WRAP_GRID
  private val colorMap = new ConwayColorMap
  reset()

  def setSize(width: Int, height: Int): Unit = {
    if (width != renderer.getWidth || height != renderer.getHeight) requestRestart(width, height)
  }

  def reset(): Unit = {
    processor = new ConwayProcessor(useParallel)
    processor.setWrap(wrapGrid, DEFAULT_WIDTH / scale, DEFAULT_HEIGHT / scale)
    renderer = new ConwayRenderer(DEFAULT_WIDTH, DEFAULT_HEIGHT, showShadows, scale, processor, colorMap)
  }

  def getWidth: Int = renderer.getWidth
  def getHeight: Int = renderer.getHeight

  def setUseContinuousIteration(continuous: Boolean): Unit = {
    this.continuousIteration = continuous
    doRender()
  }

  def setScale(scale: Int): Unit = {
    this.scale = scale
    requestRestart(renderer.getWidth, renderer.getHeight)
  }

  def getScale: Double = this.scale
  def setWrapGrid(wrap: Boolean): Unit = { this.wrapGrid = wrap }
  def setShowShadows(showShadows: Boolean) { this.showShadows = showShadows }
  def setNumStepsPerFrame(steps: Int) { this.numStepsPerFrame = steps }
  def getColormap: ColorMap = colorMap

  def setRuleType(ruleType: RuleType.RuleType) {
    processor.setRuleType(ruleType)
  }

  def setUseParallelComputation(use: Boolean) {
    useParallel = use
    processor.useParallel = use
  }

  def requestRestart() { requestRestart(renderer.getWidth, renderer.getHeight) }
  def requestNextStep() { nextStepRequested = true }
  def setAlive(i: Int, j: Int) { processor.setAlive(i, j)}
  def getNumIterations: Int = numIterations
  def getImage: BufferedImage = renderer.getImage
  def doRender() { renderer.render() }

  private def requestRestart(width: Int, height: Int): Unit = {
    try {
      numIterations = 0
      processor.setWrap(wrapGrid, width / scale, height / scale)
      renderer = new ConwayRenderer(width, height, showShadows, scale, processor, colorMap)
      restartRequested = true
    } catch {
      case e: IllegalArgumentException =>
        JOptionPane.showMessageDialog(null, e.getMessage)
    }
  }

  /**
    * @param timeStep number of rows to compute on this time step.
    * @return true when done computing whole renderer.
    */
  def timeStep(timeStep: Double): Boolean = {
    if (restartRequested) {
      restartRequested = false
      nextStepRequested = false
      numIterations = 0
      Profiler.getInstance.startCalculationTime()
      doRender()
    }
    else if (nextStepRequested || continuousIteration) {
      for (i <- 0 until numStepsPerFrame) {
        processor.nextPhase()
      }
      numIterations += 1
      doRender()
      nextStepRequested = false
    }
    false
  }
}
