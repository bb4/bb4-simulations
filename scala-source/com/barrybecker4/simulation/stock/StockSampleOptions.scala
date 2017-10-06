 // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.stock

/**
  * Defaults for stock app.
  * Options for a sample of stocks being held for some number of years.
  *
  * @author Barry Becker
  */
object StockSampleOptions {
  private val DEFAULT_NUM_STOCKS = 1
  private val DEFAULT_NUM_TIME_PERIODS = 100
  private val DEFAULT_PERCENT_INCREASE = 0.6
  private val DEFAULT_PERCENT_DECREASE = 0.4
  private val DEFAULT_STARTING_VALUE = 1000
  private val DEFAULT_X_RESOLUTION = 2
  private val DEFAULT_USE_LOG_SCALE = true
  private val DEFAULT_USE_RANDOM_CHANGE = false
}

class StockSampleOptions {
  private val numStocks = StockSampleOptions.DEFAULT_NUM_STOCKS
  private val numTimePeriods = StockSampleOptions.DEFAULT_NUM_TIME_PERIODS
  private val percentIncrease = StockSampleOptions.DEFAULT_PERCENT_INCREASE
  private val percentDecrease = StockSampleOptions.DEFAULT_PERCENT_DECREASE
  private val startingValue = StockSampleOptions.DEFAULT_STARTING_VALUE
  private val xResolution = StockSampleOptions.DEFAULT_X_RESOLUTION
  private val useLogScale = StockSampleOptions.DEFAULT_USE_LOG_SCALE
  private val useRandomChange = StockSampleOptions.DEFAULT_USE_RANDOM_CHANGE

  /**
    * This theoretical maximum is something that could occur but rarely
    * ever would by chance. One use of it is used to set an upper limit on the axis.
    * @return upper limit on sample value.
    */
  private def getTheoreticalMaximum = startingValue * Math.pow(1.0 + percentIncrease, numTimePeriods)
}
