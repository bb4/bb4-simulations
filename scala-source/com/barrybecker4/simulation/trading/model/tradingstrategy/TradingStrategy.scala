// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.tradingstrategy

import javax.swing._

import com.barrybecker4.simulation.trading.model.plugin.StrategyPlugin


/**
  * This naive strategy puts everything in the market at the start and sells it all at the end.
  * @author Barry Becker
  */
trait TradingStrategy extends StrategyPlugin {
  private var startingTotal = .0
  protected var reserve = .0
  protected var invested = .0
  protected var sharesOwned = .0
  protected var priceAtLastTransaction = .0

  def initialInvestment(stockPrice: Double, startingTotal: Double,
                        startingInvestmentPercent: Double): MarketPosition = {
    this.startingTotal = startingTotal
    assert(stockPrice > 0)
    invested = startingInvestmentPercent * startingTotal
    sharesOwned = invested / stockPrice
    reserve = startingTotal - invested
    priceAtLastTransaction = stockPrice
    MarketPosition(invested, reserve, sharesOwned)
  }

  /*** @param stockPrice stock price */
  def updateInvestment(stockPrice: Double): MarketPosition

  def finalizeInvestment(stockPrice: Double): MarketPosition = {
    val finalSell = sharesOwned * stockPrice
    reserve += finalSell
    sharesOwned = 0
    invested = 0
    MarketPosition(invested, reserve, sharesOwned)
  }

  def getGain: Double = reserve - startingTotal

  /** buy some shares */
  protected def buy(amountToInvest: Double, stockPrice: Double): Unit = {
    assert(amountToInvest > 0, "The amount to invest must be > 0. It was " + amountToInvest)
    assert(amountToInvest <= reserve)
    assert(stockPrice > 0, "The stockPrice needs to be > 0. It was " + stockPrice)
    reserve -= amountToInvest
    val sharesToBuy = amountToInvest / stockPrice
    //println(" + buying $" + amountToInvest + " which is " + sharesToBuy + " shares @" + stockPrice);
    sharesOwned += sharesToBuy
    invested = sharesOwned * stockPrice
    priceAtLastTransaction = stockPrice
  }

  /** Sell the specified number of shares at the specified price. */
  protected def sell(sharesToSell: Double, stockPrice: Double): Unit = {
    assert(sharesToSell >= 0, "you cannot sell " + sharesToSell + " shares")
    assert(sharesToSell <= sharesOwned,
      "You cannot sell more shares (" + sharesToSell + ") than you have (" + sharesOwned + ")")
    sharesOwned -= sharesToSell
    reserve += sharesToSell * stockPrice
    invested = sharesOwned * stockPrice
    priceAtLastTransaction = stockPrice
    //println(" - selling $" + (sharesToSell * stockPrice) + " which is " + sharesToSell + " shares @" + stockPrice)
  }

  /** The UI to allow the user to configure the options */
  def getOptionsUI = new JPanel

  /** Call when OK button is pressed to persist selections */
  def acceptSelectedOptions(): Unit = {}
}
