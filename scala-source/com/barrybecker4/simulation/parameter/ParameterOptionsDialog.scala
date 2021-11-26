// Copyright by Barry G. Becker, 2016-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._


/**
  * @author Barry Becker
  */
class ParameterOptionsDialog private[parameter](parent: Component, simulator: Simulator)
    extends SimulatorOptionsDialog(parent, simulator) {

  private var paramSim: ParameterSimulator = _

  /** type of distribution function to test.   */
  private var parameterChoiceField: JComboBox[String] = _
  private var showRedistribution: JCheckBox = _

  override def getTitle = "Parameter Simulation Configuration"

  override def showCustomTabByDefault: Boolean = true

  override protected def createCustomParamPanel: JPanel = {
    val paramPanel = new JPanel
    paramPanel.setLayout(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))

    paramSim = getSimulator.asInstanceOf[ParameterSimulator]
    parameterChoiceField = new JComboBox[String]()
    parameterChoiceField.setModel(
      new DefaultComboBoxModel[String](ParameterDistributionType.values.map(_.name))
    )
    showRedistribution = new JCheckBox("Show Redistribution")
    showRedistribution.setSelected(paramSim.showRedistribution)

    innerPanel.add(parameterChoiceField)
    innerPanel.add(showRedistribution)
    val fill = new JPanel
    paramPanel.add(innerPanel, BorderLayout.NORTH)
    paramPanel.add(fill, BorderLayout.CENTER)
    paramPanel
  }

  override protected def ok(): Unit = {
    super.ok()
    val simulator = getSimulator.asInstanceOf[ParameterSimulator]
    simulator.setParameter(ParameterDistributionType.fromOrdinal(parameterChoiceField.getSelectedIndex).param)
    simulator.showRedistribution = showRedistribution.isSelected
  }
}
