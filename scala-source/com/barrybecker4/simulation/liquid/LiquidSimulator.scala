// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.liquid.config.ConfigurationEnum
import com.barrybecker4.simulation.liquid.model.{Environment, LegacyLiquidEnvironment}
import com.barrybecker4.simulation.liquid.rendering.LegacyEnvironmentRenderer
import com.barrybecker4.simulation.liquid.rendering.RenderingOptions

import javax.swing.*
import java.awt.*
import scala.compiletime.uninitialized


/**
  * Main class for particle liquid simulation.
  * @author Barry Becker
  */
object LiquidSimulator {
  /** The initial time step. It may adapt. */
  private val INITIAL_TIME_STEP = 0.005
  private val BG_COLOR = Color.white
}

class LiquidSimulator() extends Simulator("Liquid") {

  private var environment: Environment = new LegacyLiquidEnvironment(ConfigurationEnum.DEFAULT_VALUE.fileName)
  private var envRenderer: LegacyEnvironmentRenderer = uninitialized
  /** These options can be changed while the simulation is running. */
  private var dynamicOptions: LiquidDynamicOptions = uninitialized
  commonInit()

  private[liquid] def loadEnvironment(configFile: String): Unit = {
    environment = new LegacyLiquidEnvironment(configFile)
    commonInit()
  }

  override protected def reset(): Unit = {
    val oldPaused = this.isPaused
    setPaused(true)
    environment.reset()
    commonInit()
    setPaused(oldPaused)
  }

  private def commonInit(): Unit = {
    initCommonUI()
    envRenderer = new LegacyEnvironmentRenderer(environment)
    val s = envRenderer.getScale.toInt
    setPreferredSize(new Dimension(environment.getWidth * s, environment.getHeight * s))
  }

  override protected def createOptionsDialog = new LiquidOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = LiquidSimulator.INITIAL_TIME_STEP
  def getEnvironment: Environment = environment
  override def setScale(scale: Double): Unit = { envRenderer.setScale(scale)}
  override def getScale: Double = envRenderer.getScale
  def getRenderingOptions: RenderingOptions = envRenderer.getRenderingOptions
  override def getBackground: Color = LiquidSimulator.BG_COLOR
  override protected def getFileNameBase: String = FileUtil.getHomeDir + "temp/animations/simulation/liquid/liquidFrame"

  /** @return a new recommended time step change. */
  override def timeStep: Double = {
    if (!isPaused) tStep = environment.stepForward(tStep)
    tStep
  }
  
  override def createDynamicControls: JPanel = {
    dynamicOptions = new LiquidDynamicOptions(this)
    dynamicOptions
  }

  override def doOptimization(): Unit = {
    throw new UnsupportedOperationException("Optimization not supported in liquid simulator.")
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    envRenderer.render(g2, getWidth, getHeight)
  }
}
