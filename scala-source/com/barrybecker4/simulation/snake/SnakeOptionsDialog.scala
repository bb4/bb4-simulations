// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.math.function.WaveType
import com.barrybecker4.simulation.common.ui.NewtonianSimOptionsDialog
import com.barrybecker4.simulation.snake.data.SnakeType
import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import scala.compiletime.uninitialized


/**
  * Use this modal dialog to let the user choose from among the
  * different snake options.
  * @author Barry Becker
  */
class SnakeOptionsDialog private[snake](parent: Component, simulator: SnakeSimulator)
  extends NewtonianSimOptionsDialog(parent, simulator) with ActionListener {

  /** type of snake to show.   */
  private var snakeCombo: JComboBox[String] = uninitialized
  private var waveTypeCombo: JComboBox[WaveType] = uninitialized
  // snake numeric param options controls
  private var waveSpeedField: NumberInput = uninitialized
  private var waveAmplitudeField: NumberInput = uninitialized
  private var wavePeriodField: NumberInput = uninitialized
  private var massScaleField: NumberInput = uninitialized
  private var springKField: NumberInput = uninitialized
  private var springDampingField: NumberInput = uninitialized

  override protected def showCustomTabByDefault: Boolean = true

  override protected def createCustomParamPanel: JPanel = {
    val customParamPanel = new JPanel(new BorderLayout)
    val snakeParamPanel = new JPanel
    snakeParamPanel.setLayout(new BoxLayout(snakeParamPanel, BoxLayout.Y_AXIS))
    snakeParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Snake Parameters"))
    snakeCombo = buildSnakeTypeCombo()
    waveTypeCombo = buildWaveTypeCombo()
    buildNumericFields(getSimulator.asInstanceOf[SnakeSimulator].getLocomotionParams)
    snakeParamPanel.add(snakeCombo)
    snakeParamPanel.add(waveTypeCombo)
    snakeParamPanel.add(waveSpeedField)
    snakeParamPanel.add(waveAmplitudeField)
    snakeParamPanel.add(wavePeriodField)
    snakeParamPanel.add(massScaleField)
    snakeParamPanel.add(springKField)
    snakeParamPanel.add(springDampingField)
    customParamPanel.add(snakeParamPanel, BorderLayout.NORTH)
    customParamPanel.add(Box.createGlue, BorderLayout.CENTER)
    customParamPanel
  }

  private def buildSnakeTypeCombo(): JComboBox[String] = {
    val snakeModel = new DefaultComboBoxModel[String](SnakeType.values.map(_.name))
    val combo = new JComboBox[String](snakeModel)
    combo.setToolTipText("Select a type of snake to show.")
    combo
  }

  private def buildWaveTypeCombo(): JComboBox[WaveType] = {
    val waveModel = new DefaultComboBoxModel[WaveType]()
    WaveType.VALUES.foreach(w => waveModel.addElement(w))
    val combo = new JComboBox[WaveType](waveModel)
    combo.setToolTipText("Select a type of wave form to use for muscle contractions.")
    combo
  }

  private def buildNumericFields(params: LocomotionParameters): Unit = {
    waveSpeedField = new NumberInput("Wave Speed (.001 slow - .9 fast):  ", params.waveSpeed,
      "This controls the speed at which the force function that travels down the body of the snake", 0.001, 0.9, false)
    waveAmplitudeField = new NumberInput("Wave Amplitude (.001 small - 2.0 large):  ", params.waveAmplitude,
      "This controls the amplitude of the force function that travels down the body of the snake", 0.001, 0.9, false)
    wavePeriodField = new NumberInput("Wave Period (0.5 small - 5.0 large):  ", params.wavePeriod,
      "This controls the period (in number of PI radians) of the force function " +
        "that travels down the body of the snake", 0.5, 5.0, false)
    massScaleField = new NumberInput("Mass Scale (.1 small - 6.0 large):  ", params.massScale,
      "This controls the overall mass of the snake. A high number indicates that the snake weighs a lot.", 0.1, 6.0, false)
    springKField = new NumberInput("Spring Stiffness  (0.1 small - 4.0 large):  ", params.springK,
      "This controls the stiffness of the springs used to make up the snake's body.", 0.1, 4.0, false)
    springDampingField = new NumberInput("Spring Damping (.1 small - 4.0 large):  ", params.springDamping,
      "This controls how quickly the spring returns to rest once released.", 0.1, 4.0, false)
  }

  override protected def ok(): Unit = {
    super.ok()
    val simulator = getSimulator.asInstanceOf[SnakeSimulator]
    val params = simulator.getLocomotionParams
    params.waveSpeed = waveSpeedField.getValue
    params.waveAmplitude = waveAmplitudeField.getValue
    params.wavePeriod = wavePeriodField.getValue
    params.massScale = massScaleField.getValue
    params.springK = springKField.getValue
    params.springDamping = springDampingField.getValue
    params.waveType = waveTypeCombo.getSelectedItem.asInstanceOf[WaveType]
    val snakeData = SnakeType.fromOrdinal(snakeCombo.getSelectedIndex).snakeData
    simulator.setSnakeData(snakeData)
  }
}
