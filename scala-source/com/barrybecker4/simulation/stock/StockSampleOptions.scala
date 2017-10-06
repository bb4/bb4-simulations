 // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.stock

/**
  * Defaults for stock app.
  * Options for a sample of stocks being held for some number of years.
  *
  * @author Barry Becker
  */
object StockSampleOptions {
  val DEFAULT_NUM_STOCKS = 1
  val DEFAULT_NUM_TIME_PERIODS = 100
  val DEFAULT_PERCENT_INCREASE = 0.6
  val DEFAULT_PERCENT_DECREASE = 0.4
  val DEFAULT_STARTING_VALUE = 1000.0
  val DEFAULT_X_RESOLUTION = 2
  val DEFAULT_USE_LOG_SCALE = true
  val DEFAULT_USE_RANDOM_CHANGE = false
}

class StockSampleOptions {
  var numStocks = StockSampleOptions.DEFAULT_NUM_STOCKS
  var numTimePeriods = StockSampleOptions.DEFAULT_NUM_TIME_PERIODS
  var percentIncrease: Double = StockSampleOptions.DEFAULT_PERCENT_INCREASE
  var percentDecrease: Double = StockSampleOptions.DEFAULT_PERCENT_DECREASE
  var startingValue: Double = StockSampleOptions.DEFAULT_STARTING_VALUE
  var xResolution = StockSampleOptions.DEFAULT_X_RESOLUTION
  var useLogScale = StockSampleOptions.DEFAULT_USE_LOG_SCALE
  var useRandomChange = StockSampleOptions.DEFAULT_USE_RANDOM_CHANGE

  /**
    * This theoretical maximum is something that could occur but rarely
    * ever would by chance. One use of it is used to set an upper limit on the axis.
    * @return upper limit on sample value.
    */
  def getTheoreticalMaximum = startingValue * Math.pow(1.0 + percentIncrease, numTimePeriods)
}
