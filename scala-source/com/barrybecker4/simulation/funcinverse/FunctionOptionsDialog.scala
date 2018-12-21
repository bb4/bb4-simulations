// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
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

  private var funcInverseSim: FunctionInverseSimulator = _

  /** type of distribution function to test.   */
  private var functionChoiceField: JComboBox[String] = _

  override def getTitle = "Function Inverse Configuration"

  override protected def createCustomParamPanel: JPanel = {
    val funcPanel = new JPanel(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))

    funcInverseSim = getSimulator.asInstanceOf[FunctionInverseSimulator]
    functionChoiceField = new JComboBox[String]()
    functionChoiceField.setModel(
      new DefaultComboBoxModel[String](FunctionType.VALUES.map(_.name))
    )

    innerPanel.add(functionChoiceField)
    val fill = new JPanel
    funcPanel.add(innerPanel, BorderLayout.NORTH)
    funcPanel.add(fill, BorderLayout.CENTER)
    funcPanel
  }

  override protected def ok(): Unit = {
    super.ok()
    val simulator = getSimulator.asInstanceOf[FunctionInverseSimulator]
    simulator.setFunction(FunctionType.VALUES(functionChoiceField.getSelectedIndex))
  }
}
