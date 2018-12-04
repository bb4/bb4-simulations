// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy

/**
  * Baser trait for generation strategies that base the next price on the last one.
  * @author Barry Becker
  */
trait IncrementalGenerationStrategy extends GenerationStrategy {

  protected def calcNewPrice(stockPrice: Double): Double = stockPrice

  def getSeries(initialValue: Double, numSteps: Int): IndexedSeq[Double] = {
    var prevPrice = initialValue
    for (i <- 0 to numSteps) yield {
      prevPrice = calcNewPrice(prevPrice)
      prevPrice
    }
  }
}
