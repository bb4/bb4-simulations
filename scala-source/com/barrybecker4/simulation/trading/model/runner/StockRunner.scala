// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.runner

import com.barrybecker4.common.math.function.HeightFunction
import com.barrybecker4.simulation.trading.options.StockGenerationOptions
import com.barrybecker4.simulation.trading.options.TradingOptions


/**
  * Given a time series representing a stock's (or collection of stocks) price, calculate the expected gain (or loss)
  * by applying some trading strategy.
  * @author Barry Becker
  */
class StockRunner(var tradingOpts: TradingOptions) {
  /**
    * @return everything about the run including the time series for the stock, the amounts invested in the stock and
    *         in the reserve account, and the amount of gain (or loss if negative) achieved by applying a certain
    *         trading strategy to a generated time series simulating a changing stock price over time.
    */
  def doRun(generationOpts: StockGenerationOptions): StockRunResult = {
    val generationStrategy = generationOpts.generationStrategy
    val tradingStrategy = tradingOpts.tradingStrategy
    var stockPrice: Double = generationOpts.startingValue
    val numPeriods = generationOpts.numTimePeriods
    // initial buy
    var position = tradingStrategy.initialInvestment(stockPrice, tradingOpts.startingTotal, tradingOpts.startingInvestmentPercent)
    val yValues = new Array[Double](numPeriods + 1)
    val investValues = new Array[Double](numPeriods + 1)
    val reserveValues = new Array[Double](numPeriods + 1)
    for (i <- 0 until numPeriods) {
      yValues(i) = stockPrice
      investValues(i) = position.getInvested
      reserveValues(i) = position.getReserve
      stockPrice = generationStrategy.calcNewPrice(stockPrice)
      position = tradingStrategy.updateInvestment(stockPrice)
    }
    position = tradingStrategy.finalizeInvestment(stockPrice)
    yValues(numPeriods) = stockPrice
    investValues(numPeriods) = 0
    reserveValues(numPeriods) = position.getReserve
    //System.out.println("*** final sell = " + finalSell
    //        + " reserve = " + reserve + " totalGain = " + totalGain + " ending stock price = " + stockPrice);
    new StockRunResult(
      new HeightFunction(yValues),
      new HeightFunction(investValues),
      new HeightFunction(reserveValues), tradingStrategy.getGain)
  }
}
