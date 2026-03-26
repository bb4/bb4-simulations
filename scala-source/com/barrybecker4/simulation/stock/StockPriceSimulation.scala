// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.stock

import scala.util.Random

/**
  * Pure simulation logic for random up/down stock price paths (coin toss each period).
  * Separated from [[StockSimulator]] for unit testing and reuse.
  */
object StockPriceSimulation {

  /**
    * Multiplier applied to price for one period given pre-drawn random values.
    * @param coin  in `[0, 1)`; heads if greater than 0.5
    * @param magnitude in [0, 1); scales the signed percent change when `useRandomChange` is true
    */
  def singlePeriodFactor(opts: StockSampleOptions, coin: Double, magnitude: Double): Double = {
    val percentChange = if (coin > 0.5) opts.percentIncrease else -opts.percentDecrease
    val delta = if (opts.useRandomChange) magnitude * percentChange else percentChange
    1.0 + delta
  }

  /** Final price for one stock after `numTimePeriods`, using explicit values per period (deterministic tests). */
  def finalPriceFromValues(opts: StockSampleOptions, coins: Seq[Double], magnitudes: Seq[Double]): Double = {
    require(coins.length == opts.numTimePeriods, "coins must have length numTimePeriods")
    if (opts.useRandomChange)
      require(magnitudes.length == opts.numTimePeriods, "magnitudes must have length numTimePeriods when useRandomChange")

    (0 until opts.numTimePeriods).foldLeft(opts.startingValue) { (price, i) =>
      val magnitude = if (opts.useRandomChange) magnitudes(i) else 0.0
      price * singlePeriodFactor(opts, coins(i), magnitude)
    }
  }

  /** Final price for one stock using [[Random]] (same contract as the original `Math.random` implementation). */
  def finalPriceWithRandom(opts: StockSampleOptions, rnd: Random): Double = {
    (0 until opts.numTimePeriods).foldLeft(opts.startingValue) { (price, _) =>
      val coin = rnd.nextDouble()
      val magnitude = if (opts.useRandomChange) rnd.nextDouble() else 0.0
      price * singlePeriodFactor(opts, coin, magnitude)
    }
  }

  /** Mean final price over `numStocks` independent paths. */
  def meanFinalPrice(opts: StockSampleOptions, rnd: Random): Double = {
    val sum = (0 until opts.numStocks).map(_ => finalPriceWithRandom(opts, rnd)).sum
    sum / opts.numStocks
  }
}
