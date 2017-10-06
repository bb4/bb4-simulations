// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy

import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import RandomUpAndDownStrategy._

import com.barrybecker4.simulation.trading1.model.generationstrategy.AbstractGenerationStrategy


/**
  * @author Barry Becker
  */
object RandomUpAndDownStrategy {
  private val DEFAULT_PERCENT_INCREASE = 0.04
  private val DEFAULT_PERCENT_DECREASE = 0.03
  private val DEFAULT_USE_RANDOM_CHANGE = true
}

class RandomUpAndDownStrategy extends AbstractGenerationStrategy {
  var percentIncrease: Double = DEFAULT_PERCENT_INCREASE
  var percentDecrease: Double = DEFAULT_PERCENT_DECREASE
  var useRandomChange: Boolean = DEFAULT_USE_RANDOM_CHANGE
  /** Amount to increase after each time period if heads   */
  private var percentIncreaseField: NumberInput = _
  /** Amount to decrease after each time period if tails  */
  private var percentDecreaseField: NumberInput = _
  /** if true changes are between 0 and percent change. */
  private var useRandomChangeCB: JCheckBox = _

  override def getName = "random ups and downs"

  override def getDescription: String = "Each time period the market goes up by some random percent or " +
    "down some sandom percent, according to a coin toss."

  override def calcNewPrice(stockPrice: Double): Double = {
    val percentChange = if (Math.random > 0.5) percentIncrease else -percentDecrease
    if (useRandomChange) stockPrice * (1.0 + Math.random * percentChange)
    else stockPrice * (1.0 + percentChange)
  }

  /** The UI to allow the user to configure the options */
  override def getOptionsUI: JPanel = {
    val strategyPanel = new JPanel
    strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.Y_AXIS))
    strategyPanel.setBorder(BorderFactory.createEtchedBorder)
    percentIncreaseField = new NumberInput("% to increase each time period if heads (0 - 100): ",
      100 * percentIncrease, "Amount to increase after each time period if coin toss is heads.", 0, 100, false)
    percentDecreaseField = new NumberInput("% to decrease each time period if tails (0 - 100): ",
      100 * percentDecrease, "Amount to decrease after each time period if coin toss is tails.", -100, 100, false)
    useRandomChangeCB = new JCheckBox("Use random change", useRandomChange)
    useRandomChangeCB.setToolTipText("If checked, " +
      "then the amount of change at each time step will be a " +
      "random amount between 0 and the percent increase or decrease.")
    percentIncreaseField.setAlignmentX(Component.CENTER_ALIGNMENT)
    percentDecreaseField.setAlignmentX(Component.CENTER_ALIGNMENT)
    useRandomChangeCB.setAlignmentX(Component.CENTER_ALIGNMENT)
    strategyPanel.add(percentIncreaseField)
    strategyPanel.add(percentDecreaseField)
    strategyPanel.add(useRandomChangeCB)
    strategyPanel
  }

  /** Call when OK button is pressed to persist selections */
  override def acceptSelectedOptions(): Unit = {
    this.percentDecrease = percentDecreaseField.getValue / 100.0
    this.percentIncrease = percentIncreaseField.getValue / 100.0
    this.useRandomChange = useRandomChangeCB.isSelected
  }
}
