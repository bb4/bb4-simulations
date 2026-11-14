// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._

import scala.compiletime.uninitialized


/**
  * Options for the parameter histogram simulator.
  */
class ParameterOptionsDialog private[parameter] (parent: Component, private val parameterSimulator: ParameterSimulator)
    extends SimulatorOptionsDialog(parent, parameterSimulator):

  /** type of distribution function to test.   */
  private var parameterChoiceField: JComboBox[String] = uninitialized
  private var showRedistribution: JCheckBox = uninitialized

  override def getTitle = "Parameter Simulation Configuration"

  override def showCustomTabByDefault: Boolean = true

  override protected def createCustomParamPanel: JPanel =
    val paramPanel = new JPanel
    paramPanel.setLayout(new BorderLayout)
    val innerPanel = new JPanel
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS))

    parameterChoiceField = new JComboBox[String]()
    parameterChoiceField.setModel(
      new DefaultComboBoxModel[String](ParameterDistributionType.values.map(_.name))
    )
    parameterChoiceField.setSelectedIndex(parameterSimulator.selectedDistributionType.ordinal)
    showRedistribution = new JCheckBox("Show Redistribution")
    showRedistribution.setSelected(parameterSimulator.showRedistribution)

    innerPanel.add(parameterChoiceField)
    innerPanel.add(showRedistribution)
    val fill = new JPanel
    paramPanel.add(innerPanel, BorderLayout.NORTH)
    paramPanel.add(fill, BorderLayout.CENTER)
    paramPanel

  override protected def ok(): Unit =
    super.ok()
    val selected = ParameterDistributionType.fromOrdinal(parameterChoiceField.getSelectedIndex)
    parameterSimulator.setDistributionType(selected)
    parameterSimulator.showRedistribution = showRedistribution.isSelected

  /** Test helpers for combo / checkbox wiring without showing the dialog. */
  private[parameter] def syncFromSimulatorForTest(): Unit =
    // Ensure custom panel widgets exist (normally created when the dialog is shown).
    if parameterChoiceField == null then createCustomParamPanel
    parameterChoiceField.setSelectedIndex(parameterSimulator.selectedDistributionType.ordinal)
    showRedistribution.setSelected(parameterSimulator.showRedistribution)

  private[parameter] def selectedComboOrdinalForTest: Int = parameterChoiceField.getSelectedIndex

  private[parameter] def applyOkForTest(): Unit = ok()

end ParameterOptionsDialog
