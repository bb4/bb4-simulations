// Copyright by Barry G. Becker, 2016 - 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui

import com.barrybecker4.simulation.habitat.creatures.populations.Habitat
import java.awt.*
import javax.swing.*
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer

object PopulationGraphPanel {
  /** Extra samples beyond visible x columns so resize/small width do not clip the series. */
  private val MaxSamplesMargin = 8
  /** Must match [[com.barrybecker4.ui.renderers.AbstractFunctionRenderer]] chart insets. */
  private val ChartMargin = 40
  private val ChartLeftMargin = 75
}

/**
  * Shows the time series graph of all the animal populations in the habitat.
  * @author Barry Becker
  */
class PopulationGraphPanel private[habitat](val habitat: Habitat) extends JPanel {

  private val graphRenderer: MultipleFunctionRenderer = habitat.createFunctionRenderer
  graphRenderer.setNumPixelsPerXPoint(2)
  private var numPixelsPerXPoint: Int = 2

  def setNumPixelsPerXPoint(numPixels: Int): Unit = {
    numPixelsPerXPoint = numPixels
    graphRenderer.setNumPixelsPerXPoint(numPixels)
  }

  override def paint(g: Graphics): Unit = {
    val w = getWidth
    if (w > 0) {
      val numXPoints =
        (w - PopulationGraphPanel.ChartMargin - PopulationGraphPanel.ChartLeftMargin).max(0) /
          numPixelsPerXPoint.max(1)
      habitat.setGraphMaxSamples(numXPoints + PopulationGraphPanel.MaxSamplesMargin)
    }
    graphRenderer.setSize(getWidth, getHeight)
    graphRenderer.paint(g)
  }
}
