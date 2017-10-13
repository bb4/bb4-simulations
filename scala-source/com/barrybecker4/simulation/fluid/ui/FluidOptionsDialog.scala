// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._
import java.awt.event.ActionListener


/**
  * Use this modal dialog to let the user choose from among the
  * different simulation options.
  * @author Bary Becker
  */
class FluidOptionsDialog private[ui](parent: Component, simulator: FluidSimulator)
  extends SimulatorOptionsDialog(parent, simulator) with ActionListener {

  override protected def createCustomParamPanel: JPanel = {
    val customParamPanel = new JPanel
    customParamPanel.setLayout(new BorderLayout)
    val fluidParamPanel = new JPanel
    fluidParamPanel.setLayout(new BoxLayout(fluidParamPanel, BoxLayout.Y_AXIS))
    fluidParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Liquid Parameters"))
    //FluidSimulator simulator = (FluidSimulator) getSimulator();
    customParamPanel.add(fluidParamPanel, BorderLayout.NORTH)
    customParamPanel
  }
}