// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui

import java.awt._
import javax.swing._

import com.barrybecker4.simulation.habitat.creatures.Populations
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer


/**
  * Shows the time series graph of all the animal populations in the habitat.
  * @author Barry Becker
  */
class PopulationGraphPanel private[habitat](val populations: Populations) extends JPanel {

  private var graphRenderer: MultipleFunctionRenderer = populations.createFunctionRenderer

  override def paint(g: Graphics): Unit = {
    graphRenderer.setSize(getWidth, getHeight)
    graphRenderer.paint(g)
  }
}
