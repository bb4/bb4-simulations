// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt._
import java.awt.event.ActionListener

import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._


/**
  * Use this modal dialog to let the user choose from among the different simulation options.
  * @author Bary Becker
  */
class WaterOptionsDialog private[ui](parent: Component, simulator: WaterSimulator)
  extends SimulatorOptionsDialog(parent, simulator) with ActionListener {

  override protected def createCustomParamPanel: JPanel = {
    val customParamPanel = new JPanel
    customParamPanel.setLayout(new BorderLayout)
    val waterParamPanel = new JPanel
    waterParamPanel.setLayout(new BoxLayout(waterParamPanel, BoxLayout.Y_AXIS))
    waterParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder,"Water Parameters"))
    customParamPanel.add(waterParamPanel, BorderLayout.NORTH)
    customParamPanel
  }

  override protected def ok(): Unit = {
    super.ok()
    // set the snake params
    val simulator = getSimulator.asInstanceOf[WaterSimulator]
  }
}
