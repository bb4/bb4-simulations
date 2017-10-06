// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.tradingstrategy

/**
  * Represents the investors current state.
  * i.e. how much is invested, how much is held in reserve, etc.
  * @author Barry Becker
  */
case class MarketPosition(invested: Double, reserve: Double, sharesOwned: Double)
