// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.tradingstrategy


/**
  * This naive strategy puts everything in the market at the start and sells it all at the end.
  *
  * @author Barry Becker
  */
class BuyAndHoldStrategy extends TradingStrategy {

  override def name = "buy and hold"
  override def description = "No transactions are made after the initial investment"

  override def updateInvestment(stockPrice: Double): MarketPosition = {
    invested = sharesOwned * stockPrice
    MarketPosition(invested, reserve, sharesOwned)
  }
}
