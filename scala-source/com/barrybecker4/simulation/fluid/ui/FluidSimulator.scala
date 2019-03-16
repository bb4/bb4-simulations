// Copyright by Barry G. Becker, 2016-20179 Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.optimization.strategy.GENETIC_SEARCH
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.fluid.model.FluidEnvironment
import com.barrybecker4.simulation.fluid.rendering.EnvironmentRenderer
import com.barrybecker4.simulation.fluid.rendering.RenderingOptions
import java.awt._
import scala.util.Random


/**
  * Simulate deep water.
  * Based on work by Jos Stam  (http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf)
  *
  * TODO: have fluid specific parameters
  *   - number of cells (x,y) - auto-calculate the scale size based on the window size.
  */
object FluidSimulator {
  val CONFIG_FILE = "com/barrybecker4/fluid/initialStateTest.data"
  val DEFAULT_STEPS_PER_FRAME = 1
  /** if true it will save all the animation steps to file */
  val INITIAL_TIME_STEP = 0.03 // initial time step

  private val BG_COLOR = Color.white
}

class FluidSimulator() extends Simulator("Fluid") {
  private val renderOptions = new RenderingOptions
  private var envRenderer: EnvironmentRenderer = _
  private var handler: InteractionHandler = _
  private val environment: FluidEnvironment = new FluidEnvironment(10, 10)

  commonInit()

  private def commonInit() {
    initCommonUI()
    val scale = renderOptions.getScale.toInt
    envRenderer = new EnvironmentRenderer(environment, renderOptions)
    setNumStepsPerFrame(FluidSimulator.DEFAULT_STEPS_PER_FRAME)
    handler = new InteractionHandler(environment, scale)
    this.addMouseListener(handler)
    this.addMouseMotionListener(handler)
  }

  override protected def createOptionsDialog = new FluidOptionsDialog(frame, this)
  override def createDynamicControls = new FluidDynamicOptions(this)

  def getRenderer: EnvironmentRenderer = envRenderer
  def getEnvironment: FluidEnvironment = environment
  def getInteractionHandler: InteractionHandler = handler
  override def getBackground: Color = FluidSimulator.BG_COLOR
  override protected def getInitialTimeStep: Double = FluidSimulator.INITIAL_TIME_STEP
  override def setScale(scale: Double) { envRenderer.getOptions.setScale(scale) }
  override def getScale: Double = envRenderer.getOptions.getScale
  def setShowVelocityVectors(show: Boolean) { envRenderer.getOptions.setShowVelocities(show)}
  def getShowVelocityVectors: Boolean = envRenderer.getOptions.getShowVelocities

  override def setPaused(bPaused: Boolean): Unit = {
    super.setPaused(bPaused)
    if (isPaused) {
      Profiler.getInstance.print()
      Profiler.getInstance.resetAll()
    }
  }

  override protected def reset(): Unit = { // remove the listeners in order to prevent a memory leak.
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

  override def doOptimization() {
    val optimizer =  new Optimizer(this)
    //, Some(FileUtil.getHomeDir + "performance/fluid/fluid_optimization.txt"))
    val params = new Array[Parameter](3)
    val paramArray = new NumericParameterArray(params, 5, new Random(1))
    setPaused(false)
    optimizer.doOptimization(GENETIC_SEARCH, paramArray, 0.3)
  }

  /** Synchronized so that paint is not called until call to timestep is done */
  override def paint(g: Graphics): Unit = synchronized {
    if (g == null) return

    val s = renderOptions.getScale
    environment.setSize((getWidth / s).toInt - 2, (getHeight / s).toInt - 2)

    val g2 = g.asInstanceOf[Graphics2D]
    Profiler.getInstance.startRenderingTime()
    envRenderer.render(g2)
    Profiler.getInstance.stopRenderingTime()
  }

  override protected def getFileNameBase: String =
    FileUtil.getHomeDir + "temp/animations/simulation/" + "fluid/fluidFrame"
}
