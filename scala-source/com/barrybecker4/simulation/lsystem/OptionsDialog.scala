// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem

import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import javax.swing.BoxLayout
import javax.swing.JPanel
import java.awt.Component
import java.awt.event.ActionEvent


/**
  * @author Barry Becker
  */
class OptionsDialog(parent: Component, simulator: Simulator) extends SimulatorOptionsDialog(parent, simulator) {

  override protected def createCustomParamPanel: JPanel = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    val sim = getSimulator.asInstanceOf[LSystemExplorer]
    panel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    val sim = getSimulator.asInstanceOf[LSystemExplorer]
  }
}
