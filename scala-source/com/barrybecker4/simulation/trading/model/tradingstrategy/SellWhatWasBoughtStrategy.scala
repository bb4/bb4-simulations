// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.tradingstrategy

import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._
import java.util
import java.util.{ArrayList, Iterator, List}


/**
  * This is the strategy suggested by John Roh.
  * The idea is to sell what was bought when the market increases by some threshold, and buy a fixed amount on dips
  *
  * Example
  * startingTotal = 100,000
  * fixedPurchasePercent = 5%
  * fixedPurchaseAmount = 5000
  * startingInvestmentPercent = 10%
  * invested = 10,000 (initially)
  *
  * If the market goes up, we do nothing - we never sell the initial investment
  * If the market goes down by lossChangePercent, then buy fixedPurchaseAmount.
  * If the market again goes down by lossChangePercent more, then buy fixedPurchaseAmount.
  * Remember these buy transactions
  * If the market ever increases gainChangePercent higher than one of those purchases, then sell it.
  * Buy whenever the marked drops by lossChangePercent from last transaction.
  *
  * @author Barry Becker
  */
class SellWhatWasBoughtStrategy extends TradingStrategy {
  private var gainThresholdPct = 0.05
  private var lossThresholdPct = 0.05
  /** This percent times the startingTotal gives the fixed purchase amount */
  private var fixedPurchasePercent = 0.05
  /**
    * This is the amount to buy on dips (assuming that much is in reserve).
    * It is computed from fixedPurchasePercent * startingTotal
    */
  private var fixedPurchaseAmount = .0
  private var transactions: Seq[Transaction] = _
  private var fixedPurchasePctField: NumberInput = _
  private var gainThresholdField: NumberInput = _
  private var lossThresholdField: NumberInput = _

  override def name = "Sell What Was Bought"
  override def description: String =
    s"Initial investment is never sold, but ${100 * fixedPurchasePercent}% " +
      s"is bought when market drops ${100 * lossThresholdPct}%, " +
    s"and that same amount is then sold when market rises ${100 * gainThresholdPct}% " +
      s"above the price that it was bought at."

  override def initialInvestment(stockPrice: Double,
                                 startingTotal: Double,
                                 startingInvestmentPercent: Double): MarketPosition = {
    fixedPurchaseAmount = fixedPurchasePercent * startingTotal
    transactions = Seq[Transaction]() //(100)
    super.initialInvestment(stockPrice, startingTotal, startingInvestmentPercent)
  }

  /**
    * if this new price triggers a transaction, then do it
    */
  override def updateInvestment(stockPrice: Double): MarketPosition = {
    // loop through all the buys and see if any of them are ready to sell.
    transactions = transactions.filter(trans => {
      val sellIt = stockPrice >= (1.0 + gainThresholdPct) * trans.stockPrice
      //println(s"numShares = ${trans.numShares}, sharesOwned = $sharesOwned")
      if (sellIt) {
        val sharesToSell = Math.min(trans.numShares, sharesOwned)
        sell(sharesToSell, stockPrice)
      }
      !sellIt
    })

    assert(priceAtLastTransaction > 0)
    assert(stockPrice > 0, "The stockPrice must be > 0")
    val pctChange =(stockPrice - priceAtLastTransaction) / priceAtLastTransaction
    if (-pctChange >= lossThresholdPct) { // buy more because its cheaper
      val amountToInvest = Math.min(reserve, fixedPurchaseAmount)
      if (amountToInvest > 0) {
        buy(amountToInvest, stockPrice)
        transactions = transactions :+ Transaction(stockPrice, amountToInvest)
      }
    }
    MarketPosition(invested, reserve, sharesOwned)
  }

  /** The UI to allow the user to configure the options */
  override def getOptionsUI: JPanel = {
    val strategyPanel = new JPanel
    strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.Y_AXIS))
    strategyPanel.setBorder(BorderFactory.createEtchedBorder)
    fixedPurchasePctField = new NumberInput("Fixed purchase percent: ",
      100 * gainThresholdPct,
      "This is the fixed amount that is bought and sold at each transaction," +
        " specified as a percent of the starting amount.",
      0, 100, false)
    gainThresholdField = new NumberInput("Gain threshold percent: ",
      100 * gainThresholdPct,
      "Triggers a sale if market goes up by this much.",
      0, 100, false)
    lossThresholdField = new NumberInput("Loss threshold percent: ",
      100 * gainThresholdPct,
      "Triggers a buy if market goes down by this much from last transaction.",
      0, 100, false)
    fixedPurchasePctField.setAlignmentX(Component.CENTER_ALIGNMENT)
    gainThresholdField.setAlignmentX(Component.CENTER_ALIGNMENT)
    lossThresholdField.setAlignmentX(Component.CENTER_ALIGNMENT)
    strategyPanel.add(fixedPurchasePctField)
    strategyPanel.add(gainThresholdField)
    strategyPanel.add(lossThresholdField)
    strategyPanel
  }

  /** Call when OK button is pressed to persist selections */
  override def acceptSelectedOptions(): Unit = {
    this.gainThresholdPct = gainThresholdField.getValue / 100.0
    this.lossThresholdPct = lossThresholdField.getValue / 100.0
    this.fixedPurchasePercent = fixedPurchasePctField.getValue / 100.0
  }
}
