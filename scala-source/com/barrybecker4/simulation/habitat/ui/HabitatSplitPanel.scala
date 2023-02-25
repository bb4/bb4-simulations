// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui

import com.barrybecker4.simulation.habitat.creatures.populations.Habitat
import java.awt.{BorderLayout, Dimension, Graphics}
import javax.swing.{JPanel, JSplitPane}


class HabitatSplitPanel(populations: Habitat) extends JPanel {

  private val graphPanel = new PopulationGraphPanel(populations)
  private val habitatPanel = new HabitatPanel(populations)
  private val splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, habitatPanel, graphPanel)
  
  override val minimumSize = new Dimension(100, 50)
  habitatPanel.setMinimumSize(minimumSize)
  graphPanel.setMinimumSize(minimumSize)
  splitPane.setDividerLocation(350)

  this.setLayout(new BorderLayout())
  this.add(splitPane, BorderLayout.CENTER)

  def setNumPixelsPerXPoint(numPixels: Int): Unit = {
    graphPanel.setNumPixelsPerXPoint(numPixels)
  }
  
  override def paint(g: Graphics): Unit = {
    splitPane.setSize(getSize)
    splitPane.paint(g)
  }
}
