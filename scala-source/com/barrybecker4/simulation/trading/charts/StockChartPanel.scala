// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.charts

import java.awt.Graphics

import com.barrybecker4.common.format.CurrencyFormatter
import com.barrybecker4.common.math.function.Function
import com.barrybecker4.simulation.trading.model.runner.StockSeries
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer
import javax.swing._

/**
  * Shows series of generated stock market simulations represented as height functions.
  * @author Barry Becker
  */
class StockChartPanel() extends JPanel {

  private val stockSeries = new StockSeries(10)
  private val stockChart = new MultipleFunctionRenderer(stockSeries)
  stockChart.setXFormatter(new CurrencyFormatter)

  def addSeries(function: Function): Unit = {
    stockSeries.append(function)
  }

  def clear(numRecentSeries: Int): Unit = { stockSeries.clear(numRecentSeries) }

  override def paint(g: Graphics): Unit = {
    stockChart.setSize(getWidth, getHeight)
    stockChart.paint(g)
  }
}
