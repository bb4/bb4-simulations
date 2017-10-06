// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options

/**
  * Defaults for graphing options.
  * There are 3 graphs: stock market, investment, and profit histogram.
  * @author Barry Becker
  */
object GraphingOptions {
  private val DEFAULT_X_RESOLUTION = 2
  private val DEFAULT_USE_LOG_SCALE = false
  private val DEFAULT_NUM_RECENT_SERIES = 100
}

class GraphingOptions {
  var histogramXResolution = GraphingOptions.DEFAULT_X_RESOLUTION
  var histogramUseLogScale = GraphingOptions.DEFAULT_USE_LOG_SCALE
  /** for stock and investment line charts, this shows some number of recent series */
  var numRecentSeries = GraphingOptions.DEFAULT_NUM_RECENT_SERIES
}
