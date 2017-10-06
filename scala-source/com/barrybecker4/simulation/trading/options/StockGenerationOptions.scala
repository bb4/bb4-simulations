// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options

import com.barrybecker4.simulation.trading1.model.generationstrategy.RandomUpsAndDownsStrategy


/**
  * Defaults for stock time series generation.
  * A single stock or a collection of stocks can be simulated.
  *
  * @author Barry Becker
  */
object StockGenerationOptions {
  val DEFAULT_GENERATION_STRATEGY = new RandomUpsAndDownsStrategy
  private val DEFAULT_NUM_STOCKS = 1
  private val DEFAULT_NUM_TIME_PERIODS = 100
  private val DEFAULT_STARTING_VALUE = 100
}

class StockGenerationOptions {
  var numStocks = StockGenerationOptions.DEFAULT_NUM_STOCKS
  var numTimePeriods = StockGenerationOptions.DEFAULT_NUM_TIME_PERIODS
  var startingValue = StockGenerationOptions.DEFAULT_STARTING_VALUE
  var generationStrategy = StockGenerationOptions.DEFAULT_GENERATION_STRATEGY
}
