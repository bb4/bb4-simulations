// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.charts

import java.awt.{Color, Graphics, Graphics2D}

import com.barrybecker4.common.format.CurrencyFormatter
import com.barrybecker4.common.math.function.Function
import com.barrybecker4.simulation.trading.model.runner.StockSeries
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer
import javax.swing._


/**
  * Shows how much is invested and how much is held in reserve over time.
  * @author Barry Becker
  */
object InvestmentChartPanel {
  private val INVESTMENT_COLOR = new Color(200, 10, 30, 80)
  private val RESERVE_COLOR = new Color(0, 100, 190, 80)
  private val TEXT_COLOR = new Color(10, 10, 80)
  private val LEGEND_X = 140
  private val LEGEND_Y = 10
  private val LEGEND_SWATCH_SIZE = 12
  private val FORMATTER = new CurrencyFormatter
}

class InvestmentChartPanel()
  extends JPanel {
  val functions = List[Function]()
  private var investmentChart = new MultipleFunctionRenderer(functions)
  private val series = new StockSeries(20)

  def addSeries(investmentFunction: Function, reserveFunction: Function): Unit = {
    series.append(investmentFunction)
    series.append(reserveFunction)
    // this should change
    val size = series.size
    var lineColors = Seq[Color]()
    for (i <- 0 until size) {
      val color = if (i % 2 == 0) InvestmentChartPanel.INVESTMENT_COLOR else InvestmentChartPanel.RESERVE_COLOR
      lineColors :+= color
    }
    investmentChart = new MultipleFunctionRenderer(series, Some(lineColors))
  }

  def clear(numRecentSeries: Int): Unit = { series.clear(numRecentSeries) }

  override def paint(g: Graphics): Unit = {
    investmentChart.setSize(getWidth, getHeight)
    investmentChart.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    drawLegend(g2)
  }

  private def drawLegend(g2: Graphics2D): Unit = {
    g2.setColor(new Color(InvestmentChartPanel.INVESTMENT_COLOR.getRGB))
    g2.fillRect(InvestmentChartPanel.LEGEND_X, InvestmentChartPanel.LEGEND_Y,
      InvestmentChartPanel.LEGEND_SWATCH_SIZE, InvestmentChartPanel.LEGEND_SWATCH_SIZE)
    g2.fillRect(InvestmentChartPanel.LEGEND_X, InvestmentChartPanel.LEGEND_Y,
      InvestmentChartPanel.LEGEND_SWATCH_SIZE, InvestmentChartPanel.LEGEND_SWATCH_SIZE)
    g2.setColor(new Color(InvestmentChartPanel.RESERVE_COLOR.getRGB))
    g2.fillRect(InvestmentChartPanel.LEGEND_X,
      InvestmentChartPanel.LEGEND_Y + InvestmentChartPanel.LEGEND_SWATCH_SIZE + 10,
      InvestmentChartPanel.LEGEND_SWATCH_SIZE, InvestmentChartPanel.LEGEND_SWATCH_SIZE)
    g2.fillRect(InvestmentChartPanel.LEGEND_X,
      InvestmentChartPanel.LEGEND_Y + InvestmentChartPanel.LEGEND_SWATCH_SIZE + 10,
      InvestmentChartPanel.LEGEND_SWATCH_SIZE, InvestmentChartPanel.LEGEND_SWATCH_SIZE)
    g2.setColor(InvestmentChartPanel.TEXT_COLOR)
    g2.drawString("Investment amount",
      InvestmentChartPanel.LEGEND_X + InvestmentChartPanel.LEGEND_SWATCH_SIZE + 10,
      InvestmentChartPanel.LEGEND_Y + 10)
    g2.drawString("Reserve amount",
      InvestmentChartPanel.LEGEND_X + InvestmentChartPanel.LEGEND_SWATCH_SIZE + 10,
      InvestmentChartPanel.LEGEND_Y + InvestmentChartPanel.LEGEND_SWATCH_SIZE + 20)
  }
}
