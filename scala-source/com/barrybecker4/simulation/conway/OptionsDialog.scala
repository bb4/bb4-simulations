// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway

import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._
import java.awt.event.ActionEvent


/**
  * @author Barry Becker
  */
class OptionsDialog(parent: Component, simulator: Simulator) extends SimulatorOptionsDialog(parent, simulator) {
  override protected def createCustomParamPanel: JPanel = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    //ConwayExplorer sim = (ConwayExplorer) getSimulator();
    panel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
  }
}
