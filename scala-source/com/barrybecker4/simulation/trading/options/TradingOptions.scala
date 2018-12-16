// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options

import com.barrybecker4.simulation.trading.model.tradingstrategy.{BuyAndHoldStrategy, TradingStrategy}


/**
  * Stock trading strategy options and default values.
  * @author Barry Becker
  */
object TradingOptions {
  val DEFAULT_TRADING_STRATEGY = new BuyAndHoldStrategy
  private val DEFAULT_STARTING_TOTAL = 100000
  private val DEFAULT_STARTING_INVESTMENT_PERCENT = 0.5
  private val DEFAULT_THEORETICAL_MAX_GAIN = 100000
}

class TradingOptions {
  var startingTotal: Double = TradingOptions.DEFAULT_STARTING_TOTAL
  var startingInvestmentPercent: Double = TradingOptions.DEFAULT_STARTING_INVESTMENT_PERCENT
  var theoreticalMaxGain: Double = TradingOptions.DEFAULT_THEORETICAL_MAX_GAIN
  var tradingStrategy: TradingStrategy = TradingOptions.DEFAULT_TRADING_STRATEGY

  def getDescription: String = tradingStrategy.toString
}
