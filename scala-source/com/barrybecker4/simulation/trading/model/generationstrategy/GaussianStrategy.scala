// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy

import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import java.util.Random


object GaussianStrategy {
  private val DEFAULT_MEAN = 0.01
  private val DEFAULT_VARIANCE = 0.1
}

/**
  * @author Barry Becker
  */
class GaussianStrategy extends IncrementalGenerationStrategy {

  var mean: Double = GaussianStrategy.DEFAULT_MEAN
  var variance: Double = GaussianStrategy.DEFAULT_VARIANCE
  /** Amount to increase after each time period if heads   */
  private var meanField: NumberInput = _
  /** Amount to decrease after each time period if tails  */
  private var varianceField: NumberInput = _
  /** random number generator */
  private val rand = new Random

  override def name = "gaussian"

  override def description: String =
    "Each time period the market goes up or down by a guassian " +
    "distributed random value whose mean and variance are specified"

  override def calcNewPrice(stockPrice: Double): Double = {
    val change = variance * rand.nextGaussian + mean
    stockPrice * Math.max(0, 1.0 + change)
  }

  /** The UI to allow the user to configure the options */
  override def getOptionsUI: JPanel = {
    val strategyPanel = new JPanel
    strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.Y_AXIS))
    strategyPanel.setBorder(BorderFactory.createEtchedBorder)
    meanField = new NumberInput("Mean of the gaussian random variable : ",
      mean, "Mean of the gaussian values generated", -1.0, 1.0, false)
    varianceField = new NumberInput("Variance of the gaussian random variable : ",
      variance, "Variance of the gaussian values generated", -100, 100, false)
    meanField.setAlignmentX(Component.CENTER_ALIGNMENT)
    varianceField.setAlignmentX(Component.CENTER_ALIGNMENT)
    strategyPanel.add(meanField)
    strategyPanel.add(varianceField)
    strategyPanel
  }

  /** Call when OK button is pressed to persist selections */
  override def acceptSelectedOptions(): Unit = {
    this.mean = meanField.getValue
    this.variance = varianceField.getValue
  }
}
