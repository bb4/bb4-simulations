// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy

import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import com.barrybecker4.simulation.trading1.model.generationstrategy.AbstractGenerationStrategy


object RandomWithAdditiveMomentumStrategy {
  private val DEFAULT_PERCENT_INCREASE = 0.04
  private val DEFAULT_PERCENT_DECREASE = 0.03
  private val DEFAULT_MOMENTUM_FACTOR = 1.0
}

/**
  * @author Barry Becker
  */
class RandomWithAdditiveMomentumStrategy extends GenerationStrategy {
  var percentIncrease: Double = RandomWithAdditiveMomentumStrategy.DEFAULT_PERCENT_INCREASE
  var percentDecrease: Double = RandomWithAdditiveMomentumStrategy.DEFAULT_PERCENT_DECREASE
  var momentumFactor: Double = RandomWithAdditiveMomentumStrategy.DEFAULT_MOMENTUM_FACTOR
  private var lastPercentChange: Double = 0
  /** Amount to increase after each time period if heads   */
  private var percentIncreaseField: NumberInput = _
  /** Amount to decrease after each time period if tails  */
  private var percentDecreaseField: NumberInput = _
  private var momentumFactorField: NumberInput = _

  override def name = "random additive momentum"
  override def description = "The random movement at each step is added to " +
    "momentumFactor time the random movement at the last step."

  override def calcNewPrice(stockPrice: Double): Double = {
    val percentChange = if (Math.random > 0.5) percentIncrease
    else -percentDecrease
    val newPercentChange = Math.random * percentChange
    val actualPercentChange: Double = momentumFactor * lastPercentChange + (1.0 - momentumFactor) * newPercentChange
    val newStockPrice = stockPrice * (1.0 + actualPercentChange)
    lastPercentChange = actualPercentChange
    newStockPrice
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
    momentumFactorField = new NumberInput("Weight to give the last percent change (0, 1):",
      momentumFactor, "The amount of weight to give the last percent change when calculating the new one. " +
        "Zero is no impact, 1 means the last value is used as is.", 0, 2, false)
    percentIncreaseField.setAlignmentX(Component.CENTER_ALIGNMENT)
    percentDecreaseField.setAlignmentX(Component.CENTER_ALIGNMENT)
    momentumFactorField.setAlignmentX(Component.CENTER_ALIGNMENT)
    strategyPanel.add(percentIncreaseField)
    strategyPanel.add(percentDecreaseField)
    strategyPanel.add(momentumFactorField)
    strategyPanel
  }

  /** Call when OK button is pressed to persist selections */
  override def acceptSelectedOptions(): Unit = {
    this.percentDecrease = percentDecreaseField.getValue / 100.0
    this.percentIncrease = percentIncreaseField.getValue / 100.0
    this.momentumFactor = momentumFactorField.getValue
  }
}
