// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy

import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import SineStrategy._


object SineStrategy { // radians
  private val DEFAULT_INCREMENT = 0.01
  private val DEFAULT_AMPLITUDE = 100
}

/**
  * @author Barry Becker
  */
class SineStrategy extends GenerationStrategy {
  /** Amount to increase after each time period if heads   */
  private var amplitudeField: NumberInput = _
  private var incrementField: NumberInput = _
  private var theta = 0.1
  private var increment = DEFAULT_INCREMENT
  private var amplitude: Double = DEFAULT_AMPLITUDE

  override def name = "Sine wave"
  override def description = "Sine Wave strategy"

  override def calcNewPrice(stockPrice: Double): Double = {
    val change = amplitude * Math.sin(theta)
    theta += increment
    Math.max(0, stockPrice + change)
  }

  /** The UI to allow the user to configure the options */
  override def getOptionsUI: JPanel = {
    val strategyPanel = new JPanel
    strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.Y_AXIS))
    strategyPanel.setBorder(BorderFactory.createEtchedBorder)
    amplitudeField = new NumberInput("Amplitude of the sin wave : ",
      amplitude, "amp", 0.0, 500.0, false)
    incrementField = new NumberInput("Increment for each step (in radians) : ",
      increment, "x increment", 0.0, 500.0, false)
    amplitudeField.setAlignmentX(Component.CENTER_ALIGNMENT)
    incrementField.setAlignmentX(Component.CENTER_ALIGNMENT)
    strategyPanel.add(amplitudeField)
    strategyPanel.add(incrementField)
    strategyPanel
  }

  /** Call when OK button is pressed to persist selections */
  override def acceptSelectedOptions(): Unit = {
    this.amplitude = amplitudeField.getValue
    this.increment = incrementField.getValue
  }
}
