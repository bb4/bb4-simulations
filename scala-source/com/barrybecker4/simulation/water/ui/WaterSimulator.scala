// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt._
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.water.model.Environment
import com.barrybecker4.simulation.water.rendering

import scala.compiletime.uninitialized


/**
  * Simulate waves in deep water.
  * Based on work by Gavin Miller (http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.89.1204&rep=rep1&type=pdf)
  * Ported from my 1991 Turbo Pascal implementation.
  * @author Barry Becker
  */
object WaterSimulator {
  val INITIAL_TIME_STEP = 0.0004
  private val BG_COLOR = Color.white
}

class WaterSimulator() extends Simulator("Water") {

  private var env: Environment = new Environment(320, 200)
  private val renderOptions = new rendering.RenderingOptions
  private var envRenderer: rendering.EnvironmentRenderer = uninitialized
  private var handler: InteractionHandler = uninitialized
  private var oldDimensions = new Dimension(0, 0)
  commonInit()

  private def commonInit(): Unit = {
    initCommonUI()
    env.reset()
    envRenderer = new rendering.EnvironmentRenderer(env, renderOptions)
    setNumStepsPerFrame(WaterDynamicOptions.DEFAULT_STEPS_PER_FRAME)
    handler = new InteractionHandler(env)
    this.addMouseListener(handler)
    this.addMouseMotionListener(handler)
  }

  override protected def createOptionsDialog = new WaterOptionsDialog(frame, this)
  override def createDynamicControls = new WaterDynamicOptions(this)

  def getRenderer: rendering.EnvironmentRenderer = envRenderer
  def getInteractionHandler: InteractionHandler = handler

  override def getBackground: Color = WaterSimulator.BG_COLOR
  override protected def getInitialTimeStep: Double = WaterSimulator.INITIAL_TIME_STEP

  def setViscosity(v: Double): Unit = {env.setViscosity(v)}
  def setShowVelocityVectors(show: Boolean): Unit = { envRenderer.getOptions.showVelocities = show}
  def getShowVelocityVectors: Boolean = envRenderer.getOptions.showVelocities

  override def setPaused(bPaused: Boolean): Unit = {
    super.setPaused(bPaused)
    if (isPaused) {
      Profiler.getInstance.print()
      Profiler.getInstance.resetAll()
    }
  }

  override protected def reset(): Unit = {
    // remove the listeners in order to prevent a memory leak.
    this.removeMouseListener(handler)
    this.removeMouseMotionListener(handler)
    commonInit()
  }

  /** @return a new recommended time step change. */
  override def timeStep: Double = synchronized {
    if (!isPaused) tStep = envRenderer.stepForward(tStep)
    tStep
  }

  override def paint(g: Graphics): Unit = synchronized {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]

    EnvSize.envDims(getWidth, getHeight, rendering.EnvironmentRenderer.OFFSET) match {
      case Some((w, h)) if w != oldDimensions.width || h != oldDimensions.height =>
        env = new Environment(w, h)
        envRenderer.setEnvironment(env)
        handler.setEnvironment(env)
        oldDimensions = new Dimension(w, h)
      case _ => // not sized yet, or unchanged
    }
    envRenderer.render(g2)
  }
}
