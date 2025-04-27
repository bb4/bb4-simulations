// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.liquid.LiquidSimulator.{INITIAL_HEIGHT, INITIAL_WIDTH}
import com.barrybecker4.simulation.liquid.config.ConfigurationEnum
import com.barrybecker4.simulation.liquid.mpm.{MpmEnvironment, WaterEnvironment}
import com.barrybecker4.simulation.liquid.rendering.{EnvironmentRenderer, RenderingOptions}

import javax.swing.*
import java.awt.*
import scala.compiletime.uninitialized


/**
  * Main class for particle liquid simulation.
  * @author Barry Becker
  */
object LiquidSimulator {
  private val BG_COLOR = Color.gray
  private val INITIAL_WIDTH = 900
  private val INITIAL_HEIGHT = 900
}

class LiquidSimulator extends Simulator("Liquid") {

  private var environment: MpmEnvironment = new WaterEnvironment(ConfigurationEnum.DEFAULT_VALUE.fileName)
  private var envRenderer: EnvironmentRenderer = uninitialized
  /** These options can be changed while the simulation is running. */
  private var dynamicOptions: LiquidDynamicOptions = uninitialized
  commonInit()

  private[liquid] def loadEnvironment(configFile: String): Unit = {
    environment = new WaterEnvironment(configFile)
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
    envRenderer = new EnvironmentRenderer(environment)

    // Add the renderer to this component to enable proper event handling
    setLayout(new BorderLayout())
    removeAll() // Clear any existing components
    add(envRenderer, BorderLayout.CENTER)

    setPreferredSize(new Dimension(environment.getWidth, environment.getHeight))
    environment.initialize()
  }

  override protected def createOptionsDialog = new LiquidOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = MpmEnvironment.DEFAULT_TIMESTEP
  def getEnvironment: MpmEnvironment = environment
  def getRenderingOptions: RenderingOptions = envRenderer.getRenderingOptions
  override def getBackground: Color = LiquidSimulator.BG_COLOR
  override protected def getFileNameBase: String = FileUtil.getHomeDir + "temp/animations/simulation/liquid/liquidFrame"

  /** @return a new recommended time step change. */
  override def timeStep: Double = {
    if (!isPaused) tStep = environment.advance()
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
    val g2 = g.asInstanceOf[Graphics2D]
    envRenderer.render(g2, getWidth, getHeight)
  }
}
