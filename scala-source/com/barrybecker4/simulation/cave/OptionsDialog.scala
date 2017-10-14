// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
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
    //val sim = getSimulator.asInstanceOf[CaveExplorer]
    panel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    //val sim = getSimulator.asInstanceOf[CaveExplorer]
  }
}
