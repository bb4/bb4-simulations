// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.options

import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._

import com.barrybecker4.simulation.habitat.HabitatSimulator


/**
  * @author Barry Becker
  */
class HabitatOptionsDialog(parent: Component, simulator: Simulator) extends SimulatorOptionsDialog(parent, simulator) {
  override protected def createCustomParamPanel: JPanel = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    val sim = getSimulator.asInstanceOf[HabitatSimulator]
    panel
  }
}