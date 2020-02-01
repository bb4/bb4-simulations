// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.runner

import com.barrybecker4.math.function.HeightFunction

/**
  * Contains the result of a stock simulation run.
  * Contains the generated stock function.
  * The final stock valuation.
  * The gain from applying a trading strategy.
  * @author Barry Becker
  */
class StockRunResult private[runner](val stockSeries: HeightFunction,
                                     val investmentSeries: HeightFunction,
                                     val reserveSeries: HeightFunction, val finalGain: Double) {
  def getFinalValuation: Double = stockSeries.getValue(1.0)
}
