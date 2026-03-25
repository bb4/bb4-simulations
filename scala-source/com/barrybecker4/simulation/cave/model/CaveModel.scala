// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model

import com.barrybecker4.simulation.cave.model.CaveProcessor.KernelType
import com.barrybecker4.simulation.cave.rendering.CaveColorMap
import com.barrybecker4.simulation.cave.rendering.CaveRenderer
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.ui.util.ColorMap
import java.awt.image.BufferedImage

import scala.compiletime.uninitialized

/**
  * Communicates changing dynamic options to CaveProcessor and controls the rendering of the cave.
  * @author Barry Becker
  */
object CaveModel {
  val DEFAULT_SCALE_FACTOR: Double = 2.0
  val DEFAULT_BUMP_HEIGHT = 0.0
  val DEFAULT_SPECULAR_PCT = 0.1
  val DEFAULT_LIGHT_SOURCE_ELEVATION: Double = math.Pi / 4.0
  val DEFAULT_LIGHT_SOURCE_AZYMUTH: Double = math.Pi / 4.0
  val DEFAULT_USE_CONTINUOUS_ITERATION = true
  val DEFAULT_NUM_STEPS_PER_FRAME = 1
  /** Radius for area of effect when doing manual modification with click/drag brush */
  val DEFAULT_BRUSH_RADIUS = 8
  /** The amount of impact that the brush has. */
  val DEFAULT_BRUSH_STRENGTH = 0.6
  private val DEFAULT_RENDERER_WIDTH = 400
  private val DEFAULT_RENDERER_HEIGHT = 400

  /** Mutable simulation parameters grouped for reuse on restart and to avoid duplicate fields. */
  case class CaveRuntimeConfig(
      floorThresh: Double = CaveProcessor.DEFAULT_FLOOR_THRESH,
      ceilThresh: Double = CaveProcessor.DEFAULT_CEIL_THRESH,
      lossFactor: Double = CaveProcessor.DEFAULT_LOSS_FACTOR,
      effectFactor: Double = CaveProcessor.DEFAULT_EFFECT_FACTOR,
      scale: Double = DEFAULT_SCALE_FACTOR,
      kernelType: KernelType = CaveProcessor.DEFAULT_KERNEL_TYPE,
      numStepsPerFrame: Int = DEFAULT_NUM_STEPS_PER_FRAME,
      useParallel: Boolean = CaveProcessor.DEFAULT_USE_PARALLEL,
      bumpHeight: Double = DEFAULT_BUMP_HEIGHT,
      specularPct: Double = DEFAULT_SPECULAR_PCT,
      lightSourceDescensionAngle: Double = DEFAULT_LIGHT_SOURCE_ELEVATION,
      lightSourceAzymuthAngle: Double = DEFAULT_LIGHT_SOURCE_AZYMUTH
  )
}

class CaveModel() {

  private var config: CaveModel.CaveRuntimeConfig = CaveModel.CaveRuntimeConfig()
  @volatile private var continuousIteration: Boolean = CaveModel.DEFAULT_USE_CONTINUOUS_ITERATION
  @volatile private var restartRequested = false
  @volatile private var nextStepRequested = false
  private var numIterations = 0
  private var cave: CaveProcessor = uninitialized
  private var renderer: CaveRenderer = uninitialized
  private val cmap = new CaveColorMap
  reset()

  def setSize(width: Int, height: Int): Unit = {
    if (width != renderer.getWidth || height != renderer.getHeight)
      requestRestart(width, height)
  }

  def reset(): Unit = {
    config = config.copy(
      floorThresh = CaveProcessor.DEFAULT_FLOOR_THRESH,
      ceilThresh = CaveProcessor.DEFAULT_CEIL_THRESH
    )
    val caveWidth = (CaveModel.DEFAULT_RENDERER_WIDTH / config.scale).toInt
    val caveHeight = (CaveModel.DEFAULT_RENDERER_HEIGHT / config.scale).toInt
    cave = new CaveProcessor(caveWidth, caveHeight, config.floorThresh, config.ceilThresh,
      config.lossFactor, config.effectFactor, config.kernelType, config.useParallel)
    renderer = new CaveRenderer(CaveModel.DEFAULT_RENDERER_WIDTH, CaveModel.DEFAULT_RENDERER_HEIGHT, cave, cmap)
  }

  def getWidth: Int = cave.getWidth
  def getHeight: Int = cave.getHeight

  def setFloorThresh(floor: Double): Unit = {
    cave.setFloorThresh(floor)
    config = config.copy(floorThresh = floor)
    doRender()
  }

  def setCeilThresh(ceil: Double): Unit = {
    cave.setCeilThresh(ceil)
    config = config.copy(ceilThresh = ceil)
    doRender()
  }

  def setLossFactor(lossFactor: Double): Unit = {
    cave.setLossFactor(lossFactor)
    config = config.copy(lossFactor = lossFactor)
  }

  def setEffectFactor(effectFactor: Double): Unit = {
    cave.setEffectFactor(effectFactor)
    config = config.copy(effectFactor = effectFactor)
  }

  def setBumpHeight(ht: Double): Unit = {
    config = config.copy(bumpHeight = ht)
    doRender()
  }

  def setSpecularPercent(pct: Double): Unit = {
    config = config.copy(specularPct = pct)
    doRender()
  }

  def setDefaultUseContinuousIteration(continuous: Boolean): Unit = {
    continuousIteration = continuous
    doRender()
  }

  def setLightSourceDescensionAngle(descensionAngle: Double): Unit = {
    config = config.copy(lightSourceDescensionAngle = descensionAngle)
    doRender()
  }

  def setLightSourceAzymuthAngle(azymuthAngle: Double): Unit = {
    config = config.copy(lightSourceAzymuthAngle = azymuthAngle)
    doRender()
  }

  def setScale(scale: Double): Unit = {
    config = config.copy(scale = scale)
    requestRestart(renderer.getWidth, renderer.getHeight)
  }

  def getScale: Double = config.scale

  def setNumStepsPerFrame(steps: Int): Unit = {
    config = config.copy(numStepsPerFrame = steps)
  }

  def setUseParallelComputation(use: Boolean): Unit = {
    config = config.copy(useParallel = use)
    cave.setUseParallel(use)
  }

  def incrementHeight(x: Int, y: Int, amount: Double): Unit = cave.incrementHeight(x, y, amount)
  def getColormap: ColorMap = cmap
  def requestRestart(): Unit = requestRestart(renderer.getWidth, renderer.getHeight)
  def requestNextStep(): Unit = nextStepRequested = true
  def getNumIterations: Int = numIterations
  def getImage: BufferedImage = renderer.getImage

  def setKernelType(kernelType: KernelType): Unit = {
    cave.setKernelType(kernelType)
    config = config.copy(kernelType = kernelType)
  }

  private def requestRestart(width: Int, height: Int): Unit = {
    try {
      val caveWidth = (width / config.scale).toInt
      val caveHeight = (height / config.scale).toInt
      cave = new CaveProcessor(caveWidth, caveHeight,
        config.floorThresh, config.ceilThresh, config.lossFactor, config.effectFactor,
        config.kernelType, config.useParallel)
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
      for (_ <- 0 until config.numStepsPerFrame) {
        cave.nextPhase()
        numIterations += 1
        doRender()
        nextStepRequested = false
      }
    }
    false
  }

  def doRender(): Unit = {
    renderer.render(
      config.bumpHeight,
      config.specularPct,
      config.lightSourceAzymuthAngle,
      config.lightSourceDescensionAngle
    )
  }
}
