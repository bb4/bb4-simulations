// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading

import com.barrybecker4.simulation.trading.model.generationstrategy.{FlatStrategy, GenerationStrategy}
import com.barrybecker4.simulation.trading.model.plugin.StrategyPlugins
import com.barrybecker4.simulation.trading.model.runner.StockRunner
import com.barrybecker4.simulation.trading.model.tradingstrategy.{BuyAndHoldStrategy, BuyPercentOfReserveStrategy}
import com.barrybecker4.simulation.trading.options.{StockGenerationOptions, TradingOptions}
import org.scalatest.funsuite.AnyFunSuite


class TradingModelTest extends AnyFunSuite {

  test("FlatStrategy getSeries: length numSteps+1 and first value is initial price") {
    val flat = new FlatStrategy
    val series = flat.getSeries(100.0, 5)
    assert(series.length == 6)
    assert(series.head == 100.0)
    assert(series.forall(_ == 100.0))
  }

  test("StockRunner buy-and-hold on flat market has zero gain") {
    val gen = new StockGenerationOptions
    gen.numTimePeriods = 10
    gen.numStocks = 1
    gen.startingValue = 100.0
    gen.generationStrategy = new FlatStrategy

    val trading = new TradingOptions
    trading.tradingStrategy = new BuyAndHoldStrategy
    trading.startingTotal = 10000.0
    trading.startingInvestmentPercent = 0.5

    val runner = new StockRunner(trading)
    val result = runner.doRun(gen)
    assert(math.abs(result.finalGain) < 1e-9)
  }

  test("BuyPercentOfReserveStrategy: loss trigger with zero reserve does not call buy(0)") {
    val s = new BuyPercentOfReserveStrategy()
    s.initialInvestment(100.0, 10000.0, 1.0)
    s.updateInvestment(97.0)
  }

  test("StrategyPlugins getStrategy throws IllegalArgumentException for unknown name") {
    val plugins = new StrategyPlugins[GenerationStrategy](
      "com.barrybecker4.simulation.trading.model.generationstrategy",
      classOf[GenerationStrategy],
      Seq(new FlatStrategy)
    )
    val ex = intercept[IllegalArgumentException] {
      plugins.getStrategy("___no_such_strategy___")
    }
    assert(ex.getMessage.contains("Unknown strategy"))
  }
}
