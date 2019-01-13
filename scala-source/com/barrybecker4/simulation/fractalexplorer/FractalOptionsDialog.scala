/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.fractalexplorer.algorithm.AlgorithmEnum
import com.barrybecker4.simulation.fractalexplorer.algorithm.JuliaAlgorithm
import com.barrybecker4.ui.components.ComplexNumberInput
import javax.swing.{BoxLayout, JComboBox, JLabel, JPanel}
import java.awt._


/**
  * @author Barry Becker
  */
class FractalOptionsDialog(val parent1: Component, val simulator: Simulator)
    extends SimulatorOptionsDialog(parent1, simulator) {

  private var algorithmChoice: JComboBox[String] = _
  private var juliaSeedField: ComplexNumberInput = _

  override protected def createRenderingParamPanel = new JPanel

  override protected def createCustomParamPanel: JPanel = {
    setResizable(true)
    val mainPanel = new JPanel(new BorderLayout)
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS))
    val label = new JLabel("Select a fractal algorithm to use:")
    algorithmChoice = createAlgorithmDropdown
    val panel = new JPanel
    panel.add(label)
    panel.add(algorithmChoice)
    val cnPanel = new JPanel
    juliaSeedField = new ComplexNumberInput("Julia Seed: ", JuliaAlgorithm.DEFAULT_JULIA_SEED)
    cnPanel.add(juliaSeedField)
    mainPanel.add(panel, BorderLayout.NORTH)
    mainPanel.add(cnPanel, BorderLayout.NORTH)
    mainPanel
  }

  /**
    * The dropdown menu at the top for selecting an algorithm for solving the puzzle.
    * @return a dropdown/down component.
    */
  private def createAlgorithmDropdown = {
    algorithmChoice = new JComboBox[String]()
    for (algorithm <- AlgorithmEnum.VALUES) {
      algorithmChoice.addItem(algorithm.getLabel)
    }
    algorithmChoice.setSelectedIndex(FractalExplorer.DEFAULT_ALGORITHM_ENUM.ordinal())
    algorithmChoice
  }

  override def getSimulator: FractalExplorer = super.getSimulator.asInstanceOf[FractalExplorer]

  override protected def ok() { // set the common rendering and global options
    val sim = getSimulator
    val selected = algorithmChoice.getSelectedIndex
    sim.setAlgorithm(AlgorithmEnum.VALUES(selected))
    println("from seed field = " + juliaSeedField.getValue)
    sim.setJuliaSeed(juliaSeedField.getValue)
    setVisible(false)
    sim.repaint()
  }
}


