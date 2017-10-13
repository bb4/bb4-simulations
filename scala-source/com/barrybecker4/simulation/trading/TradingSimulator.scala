// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.trading.charts.InvestmentChartPanel
import com.barrybecker4.simulation.trading.charts.ProfitHistogramPanel
import com.barrybecker4.simulation.trading.charts.StockChartPanel
import com.barrybecker4.simulation.trading.model.runner.StockRunResult
import com.barrybecker4.simulation.trading.model.runner.StockRunner
import com.barrybecker4.simulation.trading.options.GraphingOptions
import com.barrybecker4.simulation.trading.options.StockGenerationOptions
import com.barrybecker4.simulation.trading.options.TradingOptions
import com.barrybecker4.simulation.trading.options.ui.OptionsDialog
import com.barrybecker4.ui.animation.AnimationFrame
import com.barrybecker4.ui.util.Log
import javax.swing._
import java.awt._
import java.util.Collections


/**
  * Simulates applying a specific trading strategy to a simulated stock market time series.
  * There are strategies for both trading policies and time series generation representing the market.
  * Graphs the resulting distribution of expected profits given the assumptions of the model.
  * @author Barry Becker
  */
object TradingSimulator {
  private val TIME_STEP = 1.0
  private val DEFAULT_STEPS_PER_FRAME = 100

  def main(args: Array[String]): Unit = {
    val simulator = new TradingSimulator
    simulator.setPaused(false)
    new AnimationFrame(simulator)
  }
}

class TradingSimulator() extends Simulator("Stock Market Simulation") {
  AppContext.initialize("ENGLISH", Collections.singletonList("com.barrybecker4.ui.message"), new Log)

  private var splitPane: JSplitPane = _
  private var stockChartPanel = new StockChartPanel
  private var investmentPanel = new InvestmentChartPanel
  private var profitPanel = new ProfitHistogramPanel
  private var generationOpts = new StockGenerationOptions
  private var tradingOpts = new TradingOptions
  private var graphingOpts = new GraphingOptions
  initUI()

  def setOptions(stockSampleOpts: StockGenerationOptions, tradingOpts: TradingOptions, graphingOpts: GraphingOptions) {
    generationOpts = stockSampleOpts
    this.tradingOpts = tradingOpts
    this.graphingOpts = graphingOpts
    update()
    setNumStepsPerFrame(TradingSimulator.DEFAULT_STEPS_PER_FRAME)
  }

  override protected def reset(): Unit = {update()}

  private def initUI(): Unit = {
    val chartSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stockChartPanel, investmentPanel)
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chartSplit, profitPanel)
    //Provide minimum sizes for the two components in the split pane
    val minimumSize = new Dimension(100, 50)
    stockChartPanel.setMinimumSize(minimumSize)
    investmentPanel.setMinimumSize(minimumSize)
    profitPanel.setMinimumSize(minimumSize)
    chartSplit.setDividerLocation(350)
    splitPane.setDividerLocation(600)
    this.add(splitPane)
    update()
  }

  private def update(): Unit = {
    stockChartPanel.clear(graphingOpts.numRecentSeries)
    investmentPanel.clear(graphingOpts.numRecentSeries)
    profitPanel.setOptions(tradingOpts.theoreticalMaxGain, graphingOpts)
  }

  override protected def createOptionsDialog = new OptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = TradingSimulator.TIME_STEP
  private def getXPositionToIncrement = createSample

  override def timeStep: Double = {
    if (!isPaused) profitPanel.increment(getXPositionToIncrement)
    timeStep_
  }

  override def paint(g: Graphics): Unit = {
    splitPane.setSize(getSize)
    splitPane.paint(g)
  }

  /**
    * @return value gain achieved by applying a trading strategy to a set of numStocks after numTimePeriods.
    */
  private def createSample = {
    val runner = new StockRunner(tradingOpts)
    var total: Double = 0
    for (j <- 0 until generationOpts.numStocks) {
      val result = runner.doRun(generationOpts)
      stockChartPanel.addSeries(result.stockSeries)
      investmentPanel.addSeries(result.investmentSeries, result.reserveSeries)
      total += result.finalGain
    }
    total / generationOpts.numStocks
  }
}