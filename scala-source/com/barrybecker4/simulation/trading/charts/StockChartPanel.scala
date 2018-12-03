// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.charts

import java.awt.Graphics

import com.barrybecker4.common.format.CurrencyFormatter
import com.barrybecker4.common.math.function.Function
import com.barrybecker4.simulation.trading.model.runner.StockSeries
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer
import javax.swing._


object StockChartPanel {
  /** Sometimes the numbers on the x axis can get very large. If this is large, there will be fewer labels shown. */
  private val LABEL_WIDTH = 70
}

/**
  * Shows series of generated stock market simulations represented as height functions.
  * @author Barry Becker
  */
class StockChartPanel() extends JPanel {
  val functions = List[Function]()
  private val stockChart = new MultipleFunctionRenderer(functions)
  stockChart.setXFormatter(new CurrencyFormatter)
  private val stockSeries = new StockSeries(10)


  def addSeries(function: Function): Unit = {
    stockSeries.append(function)
    stockChart.setFunctions(stockSeries)
  }

  def clear(numRecentSeries: Int): Unit = { stockSeries.clear(numRecentSeries) }

  override def paint(g: Graphics): Unit = {
    stockChart.setSize(getWidth, getHeight)
    stockChart.paint(g)
  }
}
