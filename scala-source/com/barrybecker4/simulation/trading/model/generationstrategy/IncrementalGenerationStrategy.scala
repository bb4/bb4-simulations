// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.generationstrategy

/**
  * Base trait for generation strategies that base the next price on the last one.
  * The series has length `numSteps + 1`: index `0` is `initialValue` (starting price); each later index applies
  * `calcNewPrice` to the previous price.
  * @author Barry Becker
  */
trait IncrementalGenerationStrategy extends GenerationStrategy {

  protected def calcNewPrice(stockPrice: Double): Double = stockPrice

  def getSeries(initialValue: Double, numSteps: Int): IndexedSeq[Double] = {
    var prevPrice = initialValue
    for (i <- 0 to numSteps) yield {
      if (i == 0) initialValue
      else {
        prevPrice = calcNewPrice(prevPrice)
        prevPrice
      }
    }
  }
}
