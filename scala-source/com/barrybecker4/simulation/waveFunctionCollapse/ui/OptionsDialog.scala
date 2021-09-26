// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.ui

import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}

import java.awt.Component
import java.awt.event.ActionEvent
import javax.swing.{BoxLayout, JPanel}


/**
  * @author Barry Becker
  */
class OptionsDialog(parent: Component, simulator: Simulator) extends SimulatorOptionsDialog(parent, simulator) {
  override protected def createCustomParamPanel: JPanel = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    panel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
  }
}
