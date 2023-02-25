// Copyright by Barry G. Becker, 2016 - 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui

import com.barrybecker4.simulation.habitat.creatures.populations.Habitat
import java.awt._
import javax.swing._
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer


/**
  * Shows the time series graph of all the animal populations in the habitat.
  * @author Barry Becker
  */
class PopulationGraphPanel private[habitat](val populations: Habitat) extends JPanel {

  private val graphRenderer: MultipleFunctionRenderer = populations.createFunctionRenderer
  graphRenderer.setNumPixelsPerXPoint(2)

  def setNumPixelsPerXPoint(numPixels: Int): Unit = {
    graphRenderer.setNumPixelsPerXPoint(numPixels)
  }
  
  override def paint(g: Graphics): Unit = {
    graphRenderer.setSize(getWidth, getHeight)
    graphRenderer.paint(g)
  }
}
