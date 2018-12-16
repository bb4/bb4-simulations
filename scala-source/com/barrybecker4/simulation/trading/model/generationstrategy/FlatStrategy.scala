// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy


/**
  * @author Barry Becker
  */
class FlatStrategy extends IncrementalGenerationStrategy {

  override def name = "Flat"
  override def description = "Does not change at all over time. A horizontal line."
  override def calcNewPrice(stockPrice: Double): Double = stockPrice
}
