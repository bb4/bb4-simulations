// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.optimization.strategy.{GENETIC_SEARCH, OptimizationStrategyType}
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.liquid.config.ConfigurationEnum
import com.barrybecker4.simulation.liquid.model.LiquidEnvironment
import com.barrybecker4.simulation.liquid.rendering.EnvironmentRenderer
import com.barrybecker4.simulation.liquid.rendering.RenderingOptions
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import java.awt._
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

import scala.util.Random


/**
  * Main class for particle liquid simulation.
  * @author Barry Becker
  */
object LiquidSimulator {
  /** The initial time step. It may adapt. */
  private val INITIAL_TIME_STEP = 0.005
  private val BG_COLOR = Color.white
  private val NUM_OPT_PARAMS = 3
}

class LiquidSimulator()
  extends Simulator("Liquid") with MouseListener {

  private var environment = new LiquidEnvironment(ConfigurationEnum.DEFAULT_VALUE.fileName)
  private var envRenderer: EnvironmentRenderer = _
  /** These options can be changed while the simulation is running. */
  private var dynamicOptions: LiquidDynamicOptions = _
  private var advectionOnly = false
  commonInit()

  private[liquid] def loadEnvironment(configFile: String) = {
    environment = new LiquidEnvironment(configFile)
    environment.setAdvectionOnly(advectionOnly)
    commonInit()
  }

  override protected def reset(): Unit = {
    val oldPaused = this.isPaused
    setPaused(true)
    environment.reset()
    commonInit()
    setPaused(oldPaused)
  }

  private def commonInit() = {
    initCommonUI()
    envRenderer = new EnvironmentRenderer(environment)
    val s = envRenderer.getScale.toInt
    setPreferredSize(new Dimension(environment.getWidth * s, environment.getHeight * s))
  }

  override protected def createOptionsDialog = new LiquidOptionsDialog(frame, this)
  override protected def getInitialTimeStep = LiquidSimulator.INITIAL_TIME_STEP
  def getEnvironment: LiquidEnvironment = environment
  override def setScale(scale: Double) { envRenderer.setScale(scale)}
  override def getScale: Double = envRenderer.getScale
  def getRenderingOptions: RenderingOptions = envRenderer.getRenderingOptions
  def getSingleStepMode: Boolean = !isAnimating
  override def getBackground = LiquidSimulator.BG_COLOR
  override protected def getFileNameBase: String = FileUtil.getHomeDir + "temp/animations/simulation/liquid/liquidFrame"

  /** @return a new recommended time step change. */
  override def timeStep: Double = {
    if (!isPaused) tStep = environment.stepForward(tStep)
    tStep
  }

  def setSingleStepMode(singleStep: Boolean) {
    setAnimating(!singleStep)
    if (singleStep) addMouseListener(this)
    else removeMouseListener(this)
  }

  def getAdvectionOnly: Boolean = advectionOnly

  def setAdvectionOnly(advectOnly: Boolean) {
    advectionOnly = advectOnly
    environment.setAdvectionOnly(advectOnly)
  }

  override def createDynamicControls: JPanel = {
    dynamicOptions = new LiquidDynamicOptions(this)
    dynamicOptions
  }

  override def doOptimization() {
    val optimizer = if (GUIUtil.hasBasicService) new Optimizer(this)
                    else  new Optimizer(this, Some(FileUtil.getHomeDir + "performance/liquid/liquid_optimization.txt"))
    val params = new Array[Parameter](3)
    val paramArray = new NumericParameterArray(params, new Random(1))
    setPaused(false)
    optimizer.doOptimization(GENETIC_SEARCH, paramArray, 0.3)
  }

  override def paint(g: Graphics) {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    envRenderer.render(g2, getWidth, getHeight)
  }

  override def mouseClicked(e: MouseEvent) {
    //System.out.println("mclick timeStep="+ timeStep_ );
    environment.stepForward(tStep)
    this.repaint()
  }

  override def mousePressed(e: MouseEvent) {}
  override def mouseReleased(e: MouseEvent) {}
  override def mouseEntered(e: MouseEvent) {}
  override def mouseExited(e: MouseEvent) {}
}