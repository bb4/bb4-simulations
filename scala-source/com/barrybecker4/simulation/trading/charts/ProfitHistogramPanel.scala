// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.charts

import com.barrybecker4.common.format.CurrencyFormatter
import com.barrybecker4.math.function.LinearFunction
import com.barrybecker4.math.function.LogFunction
import com.barrybecker4.simulation.trading.options.GraphingOptions
import com.barrybecker4.ui.renderers.HistogramRenderer
import javax.swing._
import java.awt._


object ProfitHistogramPanel {
  /** Sometimes the numbers on the x axis can get very large. Scientific notation is used in those cases.
    * If this is large, there will be fewer labels shown.
    */
  private val LABEL_WIDTH = 90
}

/**
  * Show histogram of expected profit distribution given current strategy.
  * @author Barry Becker
  */
class ProfitHistogramPanel extends JPanel {
  private var histogram: HistogramRenderer = _

  def setOptions(maxGain: Double, graphingOpts: GraphingOptions): Unit = {
    val xScale = Math.pow(10, Math.max(0, Math.log10(maxGain) - graphingOpts.histogramXResolution))
    val xLogScale = 3 * graphingOpts.histogramXResolution * graphingOpts.histogramXResolution
    val maxX = (maxGain / xScale).toInt
    // go from domain to bin index
    val xFunction =
      if (graphingOpts.histogramUseLogScale) new LogFunction(xLogScale, 10.0, false)
      else new LinearFunction(1 / (1.5 * xScale), maxX / 4.0)
    val data = new Array[Int](maxX + 1)
    histogram = new HistogramRenderer(data, xFunction)
    histogram.setXFormatter(new CurrencyFormatter)
    histogram.setMaxLabelWidth(ProfitHistogramPanel.LABEL_WIDTH)
  }

  def increment(xpos: Double): Unit = {
    histogram.increment(xpos)
  }

  override def paint(g: Graphics): Unit = {
    histogram.setSize(getWidth, getHeight)
    histogram.paint(g)
  }
}
