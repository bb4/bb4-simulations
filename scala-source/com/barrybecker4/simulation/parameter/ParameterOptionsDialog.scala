// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._


/**
  * @author Barry Becker
  */
class ParameterOptionsDialog private[parameter](parent: Component, simulator: Simulator)
    extends SimulatorOptionsDialog(parent, simulator) {

  private var psim: ParameterSimulator = _
  /** type of distribution function to test.   */
  private var parameterChoiceField: JComboBox[ParameterDistributionType.Val] = _
  private var showRedistribution: JCheckBox = _

  override def getTitle = "Parameter Simulation Configuration"

  override protected def createCustomParamPanel: JPanel = {
    val paramPanel = new JPanel
    paramPanel.setLayout(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))

    psim = getSimulator.asInstanceOf[ParameterSimulator]
    parameterChoiceField = new JComboBox[ParameterDistributionType.Val]()
    parameterChoiceField.setModel(
      new DefaultComboBoxModel[ParameterDistributionType.Val](ParameterDistributionType.VALUES)
    )
    showRedistribution = new JCheckBox("Show Redistribution")
    showRedistribution.setSelected(psim.isShowRedistribution)

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
    // set the common rendering and global physics options
    simulator.setParameter(ParameterDistributionType.VALUES(parameterChoiceField.getSelectedIndex).param)
    simulator.setShowRedistribution(showRedistribution.isSelected)
  }
}
