// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.stock

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.common.format.CurrencyFormatter
import com.barrybecker4.common.math.function.LinearFunction
import com.barrybecker4.common.math.function.LogFunction
import com.barrybecker4.simulation.common.ui.DistributionSimulator
import com.barrybecker4.ui.renderers.HistogramRenderer
import com.barrybecker4.ui.util.Log
import java.util


/**
  * Simulates the N stocks over M time periods (and other options).
  * Graphs the resulting distribution of values for the sample.
  * @author Barry Becker
  */
object StockSimulator {
  /**
    * Sometime the numbers on the x axis can get very large. Scientific notation is used in those cases.
    * If this is large, there will be fewer labels shown.
    */
  private val LABEL_WIDTH = 70
}

class StockSimulator() extends DistributionSimulator("Stock Market Simulation") {

  AppContext.initialize("ENGLISH", List("com.barrybecker4.ui.message"), new Log)
  private var opts = new StockSampleOptions
  initHistogram()

  def setSampleOptions(stockSampleOptions: StockSampleOptions): Unit = {
    opts = stockSampleOptions
    initHistogram()
  }

  override protected def initHistogram(): Unit = {
    val max = opts.getTheoreticalMaximum
    val xScale: Double = Math.pow(10.0, Math.max(0, Math.log10(max) - opts.xResolution))
    val xLogScale = 3.0 * opts.xResolution * opts.xResolution
    val xFunction = if (opts.useLogScale) new LogFunction(xLogScale, 10.0, true)
    else new LinearFunction(1.0 / xScale)
    val maxX = xFunction.getValue(max).toInt
    data = Array.ofDim[Int](maxX + 1)
    histogram = new HistogramRenderer(data, xFunction)
    histogram.setXFormatter(new CurrencyFormatter)
    histogram.setMaxLabelWidth(StockSimulator.LABEL_WIDTH)
  }

  override protected def createOptionsDialog = new StockOptionsDialog(frame, this)
  override protected def getXPositionToIncrement: Double = createSample

  /** @return value of a set of numStocks after numTimePeriods. */
  private def createSample = {
    var total: Double = 0
    for (j <- 0 until opts.numStocks)
      total += calculateFinalStockPrice
    total / opts.numStocks
  }

  /** @return final stock price for a single stock after numTimePeriods. */
  private def calculateFinalStockPrice = {
    var stockPrice: Double = opts.startingValue
    for (i <- 0 until opts.numTimePeriods) {
      val percentChange = if (Math.random > 0.5) opts.percentIncrease else -opts.percentDecrease
      stockPrice *= (1.0 + (if (opts.useRandomChange) Math.random * percentChange else percentChange))
    }
    stockPrice
  }
}