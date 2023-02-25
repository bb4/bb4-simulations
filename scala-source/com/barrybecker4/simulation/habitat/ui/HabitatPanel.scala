// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui

import com.barrybecker4.simulation.habitat.creatures.populations.Habitat

import java.awt.*
import javax.swing.*


/**
  * Shows the time series graph of all the animal populations in the habitat.
 *
  * @author Barry Becker
  */
class HabitatPanel private[habitat](val populations: Habitat) extends JPanel {
  private var renderer = new HabitatRenderer(populations)

  override def paint(g: Graphics): Unit = {
    renderer.setSize(getWidth, getHeight)
    renderer.paint(g)
  }
}
