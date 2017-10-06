// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.tradingstrategy

import com.barrybecker4.simulation.trading1.options.ChangePolicy
import com.barrybecker4.simulation.trading1.options.ui.ChangePolicyPanel
import javax.swing._
import java.awt._


/**
  * When the market dips, we buy with a percentage of our reserve.
  * Conversely, when the market goes up by some threshold percent, we sell a percentage of what is invested.
  * @author Barry Becker
  */
object BuyPercentOfReserveStrategy {
  private val DEFAULT_GAIN_POLICY = new ChangePolicy(0.02, 0.05)
  private val DEFAULT_LOSS_POLICY = new ChangePolicy(0.02, 0.05)
}

class BuyPercentOfReserveStrategy extends TradingStrategy {
  private var gainPolicy = BuyPercentOfReserveStrategy.DEFAULT_GAIN_POLICY
  private var lossPolicy = BuyPercentOfReserveStrategy.DEFAULT_LOSS_POLICY
  private var gainPolicyPanel: ChangePolicyPanel = _
  private var lossPolicyPanel: ChangePolicyPanel = _

  override def getName = "percent of reserve"
  override def getDescription = "When the marked goes up, we sell a percent of investment; " +
    "when it goes down we buy a percent of reserve"

  /** if this new price triggers a transaction, then do it */
  override def updateInvestment(stockPrice: Double): MarketPosition = {
    val pctChange = (stockPrice - priceAtLastTransaction) / priceAtLastTransaction
    if (pctChange >= gainPolicy.getChangePercent) { // sell, and take some profit. Assume we can sell partial shares
      val sharesToSell = gainPolicy.getTransactPercent * sharesOwned
      sell(sharesToSell, stockPrice)
    }
    else if (-pctChange >= lossPolicy.getChangePercent) { // buy more because its cheaper
      val amountToInvest = lossPolicy.getTransactPercent * reserve
      buy(amountToInvest, stockPrice)
    }
    MarketPosition(invested, reserve, sharesOwned)
  }

  /** The UI to allow the user to configure the options */
  override def getOptionsUI: JPanel = {
    val strategyPanel = new JPanel(new BorderLayout)
    strategyPanel.setBorder(BorderFactory.createEtchedBorder)
    gainPolicyPanel = new ChangePolicyPanel(
      "% gain which triggers next transaction", "% of current investment to sell on gain", gainPolicy)
    lossPolicyPanel = new ChangePolicyPanel(
      "% market loss which triggers next transaction", "% of current reserve to use to buy on loss", lossPolicy)
    gainPolicyPanel.setAlignmentX(Component.CENTER_ALIGNMENT)
    lossPolicyPanel.setAlignmentX(Component.CENTER_ALIGNMENT)
    strategyPanel.add(gainPolicyPanel, BorderLayout.NORTH)
    strategyPanel.add(lossPolicyPanel, BorderLayout.CENTER)
    strategyPanel
  }

  /** Call when OK button is pressed to persist selections */
  override def acceptSelectedOptions(): Unit = {
    this.gainPolicy = gainPolicyPanel.getChangePolicy
    this.lossPolicy = lossPolicyPanel.getChangePolicy
  }
}