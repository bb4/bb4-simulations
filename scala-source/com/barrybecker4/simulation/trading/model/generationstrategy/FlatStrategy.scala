// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy


/**
  * @author Barry Becker
  */
class FlatStrategy extends GenerationStrategy {

  override def name = "flat"
  override def description = "Does not change at all. Horizontal line."
  override def calcNewPrice(stockPrice: Double): Double = stockPrice
}
