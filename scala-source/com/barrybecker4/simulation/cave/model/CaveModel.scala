// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model

import com.barrybecker4.simulation.cave.rendering.CaveColorMap
import com.barrybecker4.simulation.cave.rendering.CaveRenderer
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.ui.util.ColorMap
import javax.swing._
import java.awt.image.BufferedImage
import com.barrybecker4.simulation.cave.model.CaveProcessor._

/**
  * Communicates changing dynamic options to ConwayProcessor and controls the rendering of the cave.
  * @author Barry Becker
  */
object CaveModel {
  val DEFAULT_SCALE_FACTOR: Double = 2.0
  val DEFAULT_BUMP_HEIGHT = 0.0
  val DEFAULT_SPECULAR_PCT = 0.1
  val DEFAULT_LIGHT_SOURCE_ELEVATION: Double = Math.PI / 4.0
  val DEFAULT_LIGHT_SOURCE_AZYMUTH: Double = Math.PI / 4.0
  val DEFAULT_USE_CONTINUOUS_ITERATION = true
  val DEFAULT_NUM_STEPS_PER_FRAME = 1
  /** Radius for area of effect when doing manual modification with click/drag brush */
  val DEFAULT_BRUSH_RADIUS = 8
  /** The amount of impact that the brush has. */
  val DEFAULT_BRUSH_STRENGTH = 0.6
  private val DEFAULT_WIDTH = 400
  private val DEFAULT_HEIGHT = 400
}

class CaveModel() {

  private var cave: CaveProcessor = _
  private var renderer: CaveRenderer = _
  private var floorThresh = DEFAULT_FLOOR_THRESH
  private var ceilThresh = DEFAULT_CEIL_THRESH
  private var lossFactor = DEFAULT_LOSS_FACTOR
  private var effectFactor = DEFAULT_EFFECT_FACTOR
  private var scale: Double = CaveModel.DEFAULT_SCALE_FACTOR
  private var kernelType = CaveProcessor.DEFAULT_KERNEL_TYPE
  private var numStepsPerFrame = CaveModel.DEFAULT_NUM_STEPS_PER_FRAME
  private var useParallel = DEFAULT_USE_PARALLEL
  private var numIterations = 0
  private var bumpHeight = CaveModel.DEFAULT_BUMP_HEIGHT
  private var specularPct = CaveModel.DEFAULT_SPECULAR_PCT
  private var lightSourceDescensionAngle = CaveModel.DEFAULT_LIGHT_SOURCE_ELEVATION
  private var lightSourceAzymuthAngle = CaveModel.DEFAULT_LIGHT_SOURCE_AZYMUTH
  private var restartRequested = false
  private var nextStepRequested = false
  private var continuousIteration = CaveModel.DEFAULT_USE_CONTINUOUS_ITERATION
  private val cmap = new CaveColorMap
  reset()

  def setSize(width: Int, height: Int): Unit = {
    if (width != renderer.getWidth || height != renderer.getHeight)
      requestRestart(width, height)
  }

  def reset(): Unit = {
    floorThresh = DEFAULT_FLOOR_THRESH
    ceilThresh = DEFAULT_CEIL_THRESH
    val caveWidth = (CaveModel.DEFAULT_WIDTH / scale).toInt
    val caveHeight = (CaveModel.DEFAULT_HEIGHT / scale).toInt
    cave = new CaveProcessor(caveWidth, caveHeight, floorThresh, ceilThresh,
      lossFactor, effectFactor, kernelType, useParallel)
    renderer = new CaveRenderer(CaveModel.DEFAULT_WIDTH, CaveModel.DEFAULT_HEIGHT, cave, cmap)
  }

  def getWidth: Int = cave.getWidth
  def getHeight: Int = cave.getHeight

  def setFloorThresh(floor: Double): Unit = {
    cave.setFloorThresh(floor)
    this.floorThresh = floor
    doRender()
  }

  def setCeilThresh(ceil: Double): Unit = {
    cave.setCeilThresh(ceil)
    this.ceilThresh = ceil
    doRender()
  }

  def setLossFactor(lossFactor: Double): Unit = {
    cave.setLossFactor(lossFactor)
    this.lossFactor = lossFactor
  }

  def setEffectFactor(effectFactor: Double): Unit = {
    cave.setEffectFactor(effectFactor)
    this.effectFactor = effectFactor
  }

  def setBumpHeight(ht: Double): Unit = {
    this.bumpHeight = ht
    doRender()
  }

  def setSpecularPercent(pct: Double): Unit = {
    this.specularPct = pct
    doRender()
  }

  def setDefaultUseContinuousIteration(continuous: Boolean): Unit = {
    this.continuousIteration = continuous
    doRender()
  }

  def setLightSourceDescensionAngle(descensionAngle: Double): Unit = {
    this.lightSourceDescensionAngle = descensionAngle
    doRender()
  }

  def setLightSourceAzymuthAngle(azymuthAngle: Double): Unit = {
    this.lightSourceAzymuthAngle = azymuthAngle
    doRender()
  }

  def setScale(scale: Double): Unit = {
    this.scale = scale
    requestRestart(renderer.getWidth, renderer.getHeight)
  }

  def getScale: Double = this.scale
  def setNumStepsPerFrame(steps: Int): Unit =  { this.numStepsPerFrame = steps}

  def setUseParallelComputation(use: Boolean): Unit = {
    useParallel = use
    cave.setUseParallel(use)
  }

  def incrementHeight(x: Int, y: Int, amount: Double): Unit = {cave.incrementHeight(x, y, amount) }
  def getColormap: ColorMap = cmap
  def requestRestart(): Unit = { requestRestart(renderer.getWidth, renderer.getHeight) }
  def requestNextStep(): Unit = { nextStepRequested = true }
  def getNumIterations: Int = numIterations
  def getImage: BufferedImage = renderer.getImage

  def setKernelType(`type`: KernelType): Unit = {
    cave.setKernelType(`type`)
    this.kernelType = `type`
  }

  private def requestRestart(width: Int, height: Int): Unit = {
    try {
      val caveWidth = (width / scale).toInt
      val caveHeight = (height / scale).toInt
      cave = new CaveProcessor(caveWidth, caveHeight,
        floorThresh, ceilThresh, lossFactor, effectFactor, kernelType, useParallel)
      numIterations = 0
      renderer = new CaveRenderer(width, height, cave, cmap)
      restartRequested = true
    } catch {
      case e: IllegalArgumentException => javax.swing.JOptionPane.showMessageDialog(null, e.getMessage)
    }
  }

  /**
    * @param timeStep number of rows to compute on this timestep.
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
        cave.nextPhase()
        numIterations += 1
        doRender()
        nextStepRequested = false
      }
    }
    false
  }

  def doRender(): Unit =  {
    renderer.render(bumpHeight, specularPct, lightSourceAzymuthAngle, lightSourceDescensionAngle)
  }
}
