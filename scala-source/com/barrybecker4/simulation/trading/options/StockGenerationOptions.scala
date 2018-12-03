// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options

import com.barrybecker4.simulation.trading.model.generationstrategy.{GenerationStrategy, RandomUpAndDownStrategy}


/**
  * Defaults for stock time series generation.
  * A single stock or a collection of stocks can be simulated.
  * @author Barry Becker
  */
object StockGenerationOptions {
  val DEFAULT_GENERATION_STRATEGY = new RandomUpAndDownStrategy
  private val DEFAULT_NUM_STOCKS = 1
  private val DEFAULT_NUM_TIME_PERIODS = 100
  private val DEFAULT_STARTING_VALUE = 100
}

class StockGenerationOptions {
  var numStocks: Int = StockGenerationOptions.DEFAULT_NUM_STOCKS
  var numTimePeriods: Int = StockGenerationOptions.DEFAULT_NUM_TIME_PERIODS
  var startingValue: Double = StockGenerationOptions.DEFAULT_STARTING_VALUE
  var generationStrategy: GenerationStrategy = StockGenerationOptions.DEFAULT_GENERATION_STRATEGY
}
