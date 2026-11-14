// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.tradingstrategy

/**
  * Contains the purchase price and how much was bought.
  */
case class Transaction private[tradingstrategy](stockPrice: Double, amount: Double) {
  val numShares: Double = if (stockPrice == 0) 0 else amount / stockPrice
}
