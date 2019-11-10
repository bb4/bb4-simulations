/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping

import java.awt._
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}
import com.barrybecker4.simulation.complexmapping.algorithm.FunctionType
import javax.swing.{BoxLayout, JComboBox, JLabel, JPanel}


/**
  * @author Barry Becker
  */
class ComplexMappingOptionsDialog(val parent1: Component, val simulator: Simulator)
    extends SimulatorOptionsDialog(parent1, simulator) {

  private var functionChoice: JComboBox[String] = _

  override protected def createRenderingParamPanel = new JPanel

  override protected def createCustomParamPanel: JPanel = {
    setResizable(true)
    val mainPanel = new JPanel(new BorderLayout)
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS))
    val label = new JLabel("Select a fractal algorithm to use:")
    functionChoice = createFunctionDropdown
    val panel = new JPanel
    panel.add(label)
    panel.add(functionChoice)
    val cnPanel = new JPanel
    mainPanel.add(panel, BorderLayout.NORTH)
    mainPanel.add(cnPanel, BorderLayout.NORTH)
    mainPanel
  }

  /**
    * The dropdown menu at the top for selecting an algorithm for solving the puzzle.
    * @return a dropdown/down component.
    */
  private def createFunctionDropdown = {
    functionChoice = new JComboBox[String]()
    for (func <- FunctionType.VALUES) {
      functionChoice.addItem(func.name)
    }
    functionChoice.setSelectedIndex(FunctionType.VALUES.indexOf(ComplexMappingExplorer.DEFAULT_FUNCTION))
    functionChoice
  }

  override def getSimulator: ComplexMappingExplorer = super.getSimulator.asInstanceOf[ComplexMappingExplorer]

  override protected def ok(): Unit = { // set the common rendering and global options
    val sim = getSimulator
    val selected = functionChoice.getSelectedIndex
    sim.setFunction(FunctionType.VALUES(selected).function)
    setVisible(false)
    sim.repaint()
  }
}


