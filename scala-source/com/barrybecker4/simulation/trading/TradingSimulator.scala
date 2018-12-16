// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.trading.charts.InvestmentChartPanel
import com.barrybecker4.simulation.trading.charts.ProfitHistogramPanel
import com.barrybecker4.simulation.trading.charts.StockChartPanel
import com.barrybecker4.simulation.trading.model.runner.StockRunner
import com.barrybecker4.simulation.trading.options.GraphingOptions
import com.barrybecker4.simulation.trading.options.StockGenerationOptions
import com.barrybecker4.simulation.trading.options.TradingOptions
import com.barrybecker4.simulation.trading.options.ui.OptionsDialog
import com.barrybecker4.ui.animation.AnimationFrame
import com.barrybecker4.ui.util.Log
import javax.swing._
import java.awt._
import TradingSimulatorConsts._


object TradingSimulatorConsts {
  val TIME_STEP = 1.0
  val DEFAULT_STEPS_PER_FRAME = 100
}

/**
  * Simulates applying a specific trading strategy to a simulated stock market time series.
  * There are strategies for both trading policies and time series generation representing the market.
  * Graphs the resulting distribution of expected profits given the assumptions of the model.
  * @author Barry Becker
  */
object TradingSimulator extends App{
  val simulator = new TradingSimulator
  simulator.setPaused(false)
  new AnimationFrame(simulator)
}

class TradingSimulator() extends Simulator("Stock Market Simulation") {
  AppContext.initialize("ENGLISH", Seq("com.barrybecker4.ui.message").toList, new Log)

  private var splitPane: JSplitPane = _
  private val stockChartInfoPanel = new JTextArea()
  private val stockChartPanel = new StockChartPanel
  private val investmentChartInfoPanel = new JTextArea()
  private val investmentChartPanel = new InvestmentChartPanel
  private val profitPanel = new ProfitHistogramPanel
  private var generationOpts = new StockGenerationOptions
  private var tradingOpts = new TradingOptions
  private var graphingOpts = new GraphingOptions
  initUI()

  def setOptions(stockSampleOpts: StockGenerationOptions, tradingOpts: TradingOptions,
                 graphingOpts: GraphingOptions) {
    this.generationOpts = stockSampleOpts
    this.tradingOpts = tradingOpts
    this.graphingOpts = graphingOpts
    update()
    setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME)
  }

  override protected def reset(): Unit = {update()}

  private def initUI(): Unit = {

    val stockPanel = new JPanel(new BorderLayout())
    stockChartInfoPanel.setEditable(false)
    stockPanel.add(stockChartInfoPanel, BorderLayout.NORTH)
    stockPanel.add(stockChartPanel, BorderLayout.CENTER)

    val investmentPanel = new JPanel(new BorderLayout())
    investmentChartInfoPanel.setEditable(false)
    investmentPanel.add(investmentChartInfoPanel, BorderLayout.NORTH)
    investmentPanel.add(investmentChartPanel, BorderLayout.CENTER)

    val chartSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stockPanel, investmentPanel)
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chartSplit, profitPanel)

    //Provide minimum sizes for the two components in the split pane
    val minimumSize = new Dimension(100, 50)
    stockPanel.setMinimumSize(minimumSize)
    investmentPanel.setMinimumSize(minimumSize)

    profitPanel.setMinimumSize(minimumSize)
    chartSplit.setDividerLocation(350)
    splitPane.setDividerLocation(600)
    this.add(splitPane)
    update()
  }

  private def update(): Unit = {
    profitPanel.setOptions(tradingOpts.theoreticalMaxGain, graphingOpts)

    stockChartInfoPanel.setText(generationOpts.generationStrategy.name)
    stockChartInfoPanel.setToolTipText(generationOpts.getDescription)
    stockChartPanel.clear(graphingOpts.numRecentSeries)

    investmentChartInfoPanel.setText(tradingOpts.tradingStrategy.name)
    investmentChartInfoPanel.setToolTipText(tradingOpts.getDescription)
    investmentChartPanel.clear(graphingOpts.numRecentSeries)
    splitPane.repaint()
  }

  override protected def createOptionsDialog = new OptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = TIME_STEP
  private def getXPositionToIncrement = createSample

  override def timeStep: Double = {
    if (!isPaused) profitPanel.increment(getXPositionToIncrement)
    tStep
  }

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    splitPane.setSize(getSize)
  }

  /** @return value gain achieved by applying a trading strategy to a set of numStocks after numTimePeriods.*/
  private def createSample = {
    val runner = new StockRunner(tradingOpts)
    var total: Double = 0
    for (j <- 0 until generationOpts.numStocks) {
      val result = runner.doRun(generationOpts)
      stockChartPanel.addSeries(result.stockSeries)
      investmentChartPanel.addSeries(result.investmentSeries, result.reserveSeries)
      total += result.finalGain
    }
    total / generationOpts.numStocks
  }
}
