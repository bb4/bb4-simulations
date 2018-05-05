// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.common.math.WaveType
import com.barrybecker4.simulation.common.ui.NewtonianSimOptionsDialog
import com.barrybecker4.simulation.snake.data.SnakeType
import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import scala.collection.JavaConverters
import com.barrybecker4.simulation.snake.data.SnakeType.Val



/**
  * Use this modal dialog to let the user choose from among the
  * different snake options.
  * @author Barry Becker
  */
class SnakeOptionsDialog private[snake](parent: Component, simulator: SnakeSimulator)
  extends NewtonianSimOptionsDialog(parent, simulator) with ActionListener {

  /** type of snake to show.   */
  private var snakeCombo: JComboBox[String] = _
  private var waveTypeCombo: JComboBox[WaveType] = _
  // snake numeric param options controls
  private var waveSpeedField: NumberInput = _
  private var waveAmplitudeField: NumberInput = _
  private var wavePeriodField: NumberInput = _
  private var massScaleField: NumberInput = _
  private var springKField: NumberInput = _
  private var springDampingField: NumberInput = _

  override protected def createCustomParamPanel: JPanel = {
    val customParamPanel = new JPanel
    customParamPanel.setLayout(new BorderLayout)
    val snakeParamPanel = new JPanel
    snakeParamPanel.setLayout(new BoxLayout(snakeParamPanel, BoxLayout.Y_AXIS))
    snakeParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Snake Parameters"))
    val snakeModel = new DefaultComboBoxModel[String](SnakeType.VALUES.map(_.name))
    snakeCombo = new JComboBox[String](snakeModel)
    snakeCombo.setToolTipText("Select a type of snake to show.")
    val waveModel = new DefaultComboBoxModel[WaveType]()
    WaveType.VALUES.foreach(w => waveModel.addElement(w))
    waveTypeCombo = new JComboBox[WaveType](waveModel)
    waveTypeCombo.setToolTipText("Select a type of wave form to use for muscle contractions.")
    val params = getSimulator.asInstanceOf[SnakeSimulator].getLocomotionParams

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

  override protected def ok(): Unit = {
    super.ok()
    // set the snake params
    val simulator = getSimulator.asInstanceOf[SnakeSimulator]
    val params = simulator.getLocomotionParams
    params.waveSpeed = waveSpeedField.getValue
    params.waveAmplitude = waveAmplitudeField.getValue
    params.wavePeriod = wavePeriodField.getValue
    params.massScale = massScaleField.getValue
    params.springK = springKField.getValue
    params.springDamping = springDampingField.getValue
    params.waveType = waveTypeCombo.getSelectedItem.asInstanceOf[WaveType]
    val snakeData = SnakeType.VALUES(snakeCombo.getSelectedIndex).snakeData
    simulator.setSnakeData(snakeData)
  }
}