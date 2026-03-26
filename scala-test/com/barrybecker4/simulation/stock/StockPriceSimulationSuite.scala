// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.stock

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class StockPriceSimulationSuite extends AnyFunSuite {

  test("getTheoreticalMaximum is startingValue * (1 + percentIncrease)^numTimePeriods") {
    val opts = new StockSampleOptions
    opts.startingValue = 1000.0
    opts.percentIncrease = 0.6
    opts.numTimePeriods = 100
    val expected = 1000.0 * math.pow(1.6, 100)
    assert(opts.getTheoreticalMaximum === expected)
  }

  test("singlePeriodFactor heads vs tails without random change") {
    val opts = new StockSampleOptions
    opts.percentIncrease = 0.6
    opts.percentDecrease = 0.4
    opts.useRandomChange = false
    assert(StockPriceSimulation.singlePeriodFactor(opts, 0.51, 0.0) === 1.6)
    assert(StockPriceSimulation.singlePeriodFactor(opts, 0.49, 0.0) === 0.6)
  }

  test("singlePeriodFactor with random change scales by magnitude") {
    val opts = new StockSampleOptions
    opts.percentIncrease = 0.6
    opts.percentDecrease = 0.4
    opts.useRandomChange = true
    assert(StockPriceSimulation.singlePeriodFactor(opts, 0.51, 0.5) === 1.0 + 0.5 * 0.6)
    assert(StockPriceSimulation.singlePeriodFactor(opts, 0.49, 0.25) === 1.0 - 0.25 * 0.4)
  }

  test("finalPriceFromValues matches two-period path without random change") {
    val opts = new StockSampleOptions
    opts.startingValue = 1000.0
    opts.numTimePeriods = 2
    opts.percentIncrease = 0.6
    opts.percentDecrease = 0.4
    opts.useRandomChange = false
    val coins = Seq(0.6, 0.4)
    val p = StockPriceSimulation.finalPriceFromValues(opts, coins, Seq.empty)
    assert(p === 1000.0 * 1.6 * 0.6)
  }

  test("finalPriceWithRandom matches finalPriceFromValues for same draws") {
    val opts = new StockSampleOptions
    opts.startingValue = 100.0
    opts.numTimePeriods = 3
    opts.percentIncrease = 0.1
    opts.percentDecrease = 0.05
    opts.useRandomChange = true
    val rnd = new Random(12345L)
    val draws = (0 until opts.numTimePeriods * 2).map(_ => rnd.nextDouble())
    val coins = (0 until opts.numTimePeriods).map(i => draws(2 * i))
    val mags = (0 until opts.numTimePeriods).map(i => draws(2 * i + 1))
    val rnd2 = new Random(12345L)
    val fromRandom = StockPriceSimulation.finalPriceWithRandom(opts, rnd2)
    val fromValues = StockPriceSimulation.finalPriceFromValues(opts, coins, mags)
    assert(fromRandom === fromValues)
  }

  test("meanFinalPrice averages independent paths") {
    val opts = new StockSampleOptions
    opts.numStocks = 4
    opts.numTimePeriods = 1
    opts.startingValue = 10.0
    opts.percentIncrease = 0.2
    opts.percentDecrease = 0.1
    opts.useRandomChange = false
    val rnd = new Random(999L)
    val prices = (0 until opts.numStocks).map(_ => StockPriceSimulation.finalPriceWithRandom(opts, rnd))
    val rnd2 = new Random(999L)
    val mean = StockPriceSimulation.meanFinalPrice(opts, rnd2)
    assert(mean === prices.sum / prices.length)
  }
}
