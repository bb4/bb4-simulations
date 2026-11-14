// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.liquid.Logger
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.fluid.model.FluidEnvironment
import com.barrybecker4.simulation.fluid.rendering.EnvironmentRenderer
import com.barrybecker4.simulation.fluid.rendering.RenderingOptions
import java.awt._
import scala.compiletime.uninitialized


/**
  * Simulate deep water.
  * Based on work by Jos Stam  (http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf)
  */
object FluidSimulator {
  val DEFAULT_STEPS_PER_FRAME = 1
  val INITIAL_TIME_STEP = 0.03

  private val BG_COLOR = Color.white
}

class FluidSimulator() extends Simulator("Fluid") {
  private val renderOptions = new RenderingOptions
  private var envRenderer: EnvironmentRenderer = uninitialized
  private var handler: InteractionHandler = uninitialized
  private val environment: FluidEnvironment = new FluidEnvironment(10, 10)
  private var lastCellW = -1
  private var lastCellH = -1

  commonInit()

  private def commonInit(): Unit = {
    initCommonUI()
    val scale = renderOptions.scale
    envRenderer = new EnvironmentRenderer(environment, renderOptions)
    setNumStepsPerFrame(FluidSimulator.DEFAULT_STEPS_PER_FRAME)

    val force: Double = if (handler != null) handler.force else InteractionHandler.DEFAULT_FORCE
    handler = new InteractionHandler(environment, scale, force, EnvironmentRenderer.ContentOffsetPixels)
    this.addMouseListener(handler)
    this.addMouseMotionListener(handler)
    lastCellW = -1
    lastCellH = -1
  }

  override protected def createOptionsDialog = new FluidOptionsDialog(frame, this)
  override def createDynamicControls = new FluidDynamicOptions(this)

  def getRenderer: EnvironmentRenderer = envRenderer
  def getEnvironment: FluidEnvironment = environment
  def getInteractionHandler: InteractionHandler = handler
  override def getBackground: Color = FluidSimulator.BG_COLOR
  override protected def getInitialTimeStep: Double = FluidSimulator.INITIAL_TIME_STEP
  override def setScale(scale: Double): Unit = {
    renderOptions.scale = scale
    handler.setScale(scale)
    envRenderer.setEnvironment(environment)
  }
  override def getScale: Double = renderOptions.scale

  override def setPaused(bPaused: Boolean): Unit = {
    super.setPaused(bPaused)
    if (isPaused) {
      Profiler.getInstance.print()
      Profiler.getInstance.resetAll()
    }
  }

  override protected def reset(): Unit = {
    this.removeMouseListener(handler)
    this.removeMouseMotionListener(handler)
    environment.reset()
    commonInit()
  }

  /** @return a new recommended time step change. */
  override def timeStep: Double = synchronized {
    if (!isPaused) {
      tStep = environment.stepForward(tStep)
    }
    tStep
  }

  override def doOptimization(): Unit = {
    Logger.log(1, "Fluid parameter optimization is not configured (no Parameter descriptors).")
  }

  /** Synchronized so that paint is not called until call to timestep is done */
  override def paint(g: Graphics): Unit = synchronized {
    if (g == null) return

    val s = renderOptions.scale
    val cellW = math.max(1, (getWidth / s).toInt - 2)
    val cellH = math.max(1, (getHeight / s).toInt - 2)
    if (cellW != lastCellW || cellH != lastCellH) {
      environment.setSize(cellW, cellH)
      envRenderer.setEnvironment(environment)
      handler.setEnvironment(environment)
      handler.setScale(s)
      lastCellW = cellW
      lastCellH = cellH
    }

    val g2 = g.asInstanceOf[Graphics2D]
    Profiler.getInstance.startRenderingTime()
    envRenderer.render(g2)
    Profiler.getInstance.stopRenderingTime()
  }

  override protected def getFileNameBase: String =
    FileUtil.getHomeDir + "temp/animations/simulation/" + "fluid/fluidFrame"
}
