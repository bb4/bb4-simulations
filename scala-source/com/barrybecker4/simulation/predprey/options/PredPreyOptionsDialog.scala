// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.options

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.predprey1.PredPreySimulator
import javax.swing._
import java.awt._


/**
  * @author Barry Becker
  */
class PredPreyOptionsDialog(parent: Component, simulator: Simulator) extends SimulatorOptionsDialog(parent, simulator) {

  override protected def createCustomParamPanel: JPanel = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    val sim = getSimulator.asInstanceOf[PredPreySimulator]
    panel
  }
}