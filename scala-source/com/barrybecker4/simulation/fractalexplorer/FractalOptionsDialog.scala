/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.fractalexplorer.algorithm.AlgorithmEnum
import com.barrybecker4.simulation.fractalexplorer.algorithm.JuliaAlgorithm
import com.barrybecker4.ui.components.ComplexNumberInput
import javax.swing.{BoxLayout, JComboBox, JLabel, JPanel}
import java.awt._

import scala.compiletime.uninitialized


/**
  * @author Barry Becker
  */
class FractalOptionsDialog(parent1: Component, simulator: Simulator)
    extends SimulatorOptionsDialog(parent1, simulator) {

  private var algorithmChoice: JComboBox[String] = uninitialized
  private var juliaSeedField: ComplexNumberInput = uninitialized

  override protected def createRenderingParamPanel = new JPanel

  override protected def showCustomTabByDefault: Boolean = true

  override protected def createCustomParamPanel: JPanel = {
    setResizable(true)
    val mainPanel = new JPanel
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS))

    algorithmChoice = new JComboBox[String]()
    for (algorithm <- AlgorithmEnum.VALUES)
      algorithmChoice.addItem(algorithm.getLabel)
    algorithmChoice.setSelectedIndex(FractalExplorer.DEFAULT_ALGORITHM_ENUM.ordinal)

    val panel = new JPanel
    panel.add(new JLabel("Select a fractal algorithm to use:"))
    panel.add(algorithmChoice)

    juliaSeedField = new ComplexNumberInput("Julia Seed: ", JuliaAlgorithm.DEFAULT_JULIA_SEED)
    val cnPanel = new JPanel
    cnPanel.add(juliaSeedField)

    mainPanel.add(panel)
    mainPanel.add(cnPanel)
    mainPanel
  }

  override def getSimulator: FractalExplorer = super.getSimulator.asInstanceOf[FractalExplorer]

  override protected def ok(): Unit = {
    val sim = getSimulator
    sim.setAlgorithm(AlgorithmEnum.VALUES(algorithmChoice.getSelectedIndex))
    sim.setJuliaSeed(juliaSeedField.getValue)
    setVisible(false)
    sim.repaint()
  }
}
