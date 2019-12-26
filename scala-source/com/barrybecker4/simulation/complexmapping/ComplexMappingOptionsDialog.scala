/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping

import java.awt._
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}
import javax.swing.{BoxLayout, JComboBox, JLabel, JPanel}


/**
  * @author Barry Becker
  */
class ComplexMappingOptionsDialog(val parent1: Component, val simulator: Simulator)
    extends SimulatorOptionsDialog(parent1, simulator) {

  override protected def createRenderingParamPanel = new JPanel

  override protected def createCustomParamPanel: JPanel = {
    setResizable(true)
    val mainPanel = new JPanel(new BorderLayout)
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS))
    val panel = new JPanel
    mainPanel.add(panel, BorderLayout.NORTH)
    mainPanel
  }

  override def getSimulator: ComplexMappingExplorer = super.getSimulator.asInstanceOf[ComplexMappingExplorer]

  override protected def ok(): Unit = { // set the common rendering and global options
    val sim = getSimulator
    setVisible(false)
    sim.repaint()
  }
}


