// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.optimization.optimizee.Optimizee
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.ui.animation.AnimationComponent
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.util.GUIUtil
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener


/**
  * Base class for all simulations.
  * Extends from  because simulation involve showing an animation of a process.
  * @param name the name of the simulator (eg Snake, Liquid, or Trebuchet)
  * @author Barry Becker
  */
abstract class Simulator(val name: String) extends AnimationComponent with Optimizee {

  protected var tStep = getInitialTimeStep
  private var optionsDialog: SimulatorOptionsDialog = _
  protected var frame: JFrame = _
  setName(name)

  /** whether or not to use anti-aliasing when rendering */
  private var useAntialiasing = true

  protected def initCommonUI(): Unit = {
    GUIUtil.setCustomLookAndFeel()
  }

  protected def getInitialTimeStep: Double

  def setTimeStep(timeStep: Double): Unit = {
    tStep = timeStep
  }

  def getTimeStep: Double = tStep

  def setAntialiasing(use: Boolean): Unit = {
    useAntialiasing = use
  }

  def getAntialiasing: Boolean = useAntialiasing

  def setScale(scale: Double): Unit = {
  }

  def getScale: Double = 1.0

  protected def createOptionsButton: GradientButton = {
    val button = new GradientButton("Options")
    optionsDialog = createOptionsDialog
    button.addActionListener((e: ActionEvent) => {
      optionsDialog.setLocationRelativeTo(e.getSource.asInstanceOf[Component])
      // pause the snake while the options are open
      val simulator = optionsDialog.getSimulator
      val oldPauseVal = simulator.isPaused
      simulator.setPaused(true)
      optionsDialog.showDialog
      simulator.setPaused(oldPauseVal)
    })
    button
  }

  protected def createOptionsDialog: SimulatorOptionsDialog

  /** @return to the initial state. */
  protected def reset(): Unit

  def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createStartButton)
    controls.add(createResetButton)
    controls.add(createOptionsButton)
    controls
  }

  override protected def getFileNameBase: String =
    "undefined (can't call getHomeDir from applet without extra security)."
    //FileUtil.getHomeDir + "temp/animations/simulation/" + getClass.getName

  /** @return a reset button that allows you to restore the initial condition of the simulation.*/
  protected def createResetButton: JButton = {
    val resetButton = new JButton("Reset")
    resetButton.addActionListener(e => reset())
    resetButton
  }

  /** Override this to return ui elements that can be used to modify the simulation as it is running. */
  def createDynamicControls: JPanel = null

  override protected def getStatusMessage: String = "frames/second=" + FormatUtil.formatNumber(getFrameRate)

  def doOptimization(): Unit = {
    println("not implemented for this simulator")
  }

  override def evaluateByComparison = false

  /** part of the Optimizee interface */
  override def getOptimalFitness = 0
  override def compareFitness(params1: ParameterArray, params2: ParameterArray): Double = evaluateFitness(params1) - evaluateFitness(params2)

  /**
    * *** implements the key method of the Optimizee interface
    * evaluates the fitness.
    */
  override def evaluateFitness(params: ParameterArray): Double = {
    assert(false, "not implemented yet")
    0.0
  }
}
