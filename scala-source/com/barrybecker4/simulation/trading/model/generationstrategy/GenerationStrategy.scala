// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy

import javax.swing._

import com.barrybecker4.simulation.trading.model.plugin.StrategyPlugin


/**
  * Defines how a stock series should be generated.
  * There are various strategies that we might employ varying from simple to sophisticated.
  * The default naive strategy puts everything in the market at the start and sells it all at the end.
  *
  *
  * @author Barry Becker
  */
trait GenerationStrategy extends StrategyPlugin {
  def calcNewPrice(stockPrice: Double): Double

  /** The UI to allow the user to configure the generation options */
  def getOptionsUI: JPanel = new JPanel

  /** Call when OK button is pressed to persist selections */
  def acceptSelectedOptions(): Unit = {}

  override def toString: String = name + ": " + description
}
