// Copyright by Barry G. Becker, 2016-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._


/**
  * @author Barry Becker
  */
class FunctionOptionsDialog(parent: Component, simulator: Simulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  private var paramSim: FunctionInverseSimulator = _

  /** type of distribution function to test.   */
  private var parameterChoiceField: JComboBox[String] = _

  override def getTitle = "Parameter Simulation Configuration"

  override protected def createCustomParamPanel: JPanel = {
    val paramPanel = new JPanel
    paramPanel.setLayout(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))

    paramSim = getSimulator.asInstanceOf[FunctionInverseSimulator]
    parameterChoiceField = new JComboBox[String]()
    parameterChoiceField.setModel(
      new DefaultComboBoxModel[String](FunctionType.VALUES.map(_.name))
    )


    innerPanel.add(parameterChoiceField)
    val fill = new JPanel
    paramPanel.add(innerPanel, BorderLayout.NORTH)
    paramPanel.add(fill, BorderLayout.CENTER)
    paramPanel
  }

  override protected def ok(): Unit = {
    super.ok()
    val simulator = getSimulator.asInstanceOf[FunctionInverseSimulator]
    simulator.setFunction(FunctionType.VALUES(parameterChoiceField.getSelectedIndex).func)
  }
}
