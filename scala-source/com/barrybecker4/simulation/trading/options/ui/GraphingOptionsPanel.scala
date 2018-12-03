// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options.ui

import com.barrybecker4.simulation.trading.options.GraphingOptions
import com.barrybecker4.ui.components.NumberInput
import javax.swing._


/**
  * Show series history for line charts
  * @author Barry Becker
  */
class GraphingOptionsPanel() extends JPanel {
  private val graphOptions: GraphingOptions = new GraphingOptions
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  /** Granularity fo the histogram bins on the x axis.  */
  private val xResolutionField =
    new NumberInput("Histogram Resolution (1 - 5): ", graphOptions.histogramXResolution,
      "1 is low resolution 5 is high (meaning more bins on the x axis).",
      1, 5, true)
  /** if true the x axis will have a log scale */
  private val useLogScale = new JCheckBox("Use log scale on x axis", graphOptions.histogramUseLogScale)
  useLogScale.setToolTipText(
    "If checked, the x axis will be shown on a log scale so that the histogram will be easier to interpret.")
  private var numRecentSeriesField =
    new NumberInput("Num recent series: ", graphOptions.numRecentSeries,
      "The number of recent time series to show in the stock generation and investment line charts on the left",
      1, 1000, true)

  add(xResolutionField)
  add(useLogScale)
  add(numRecentSeriesField)
  setBorder(Section.createBorder("Graphing Options"))

  private[ui] def getOptions = {
    graphOptions.histogramXResolution = xResolutionField.getIntValue
    graphOptions.histogramUseLogScale = useLogScale.isSelected
    graphOptions.numRecentSeries = numRecentSeriesField.getIntValue
    graphOptions
  }
}