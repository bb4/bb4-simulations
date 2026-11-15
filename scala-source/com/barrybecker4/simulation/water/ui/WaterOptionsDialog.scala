// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt._
import java.awt.event.ActionListener

import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.water.model.InitialEnvironment
import javax.swing._

import scala.compiletime.uninitialized


/**
  * Use this modal dialog to let the user choose from among the different simulation options.
  * @author Barry Becker
  */
class WaterOptionsDialog private[ui](parent: Component, simulator: WaterSimulator)
  extends SimulatorOptionsDialog(parent, simulator) with ActionListener {

  private var initialEnvCombo: JComboBox[String] = uninitialized

  override protected def createCustomParamPanel: JPanel = {
    val customParamPanel = new JPanel
    customParamPanel.setLayout(new BorderLayout)
    val waterParamPanel = new JPanel
    waterParamPanel.setLayout(new BoxLayout(waterParamPanel, BoxLayout.Y_AXIS))
    waterParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Water Parameters"))
    initialEnvCombo = buildInitialEnvCombo()
    waterParamPanel.add(new JLabel("Initial Environment:"))
    waterParamPanel.add(initialEnvCombo)
    customParamPanel.add(waterParamPanel, BorderLayout.NORTH)
    customParamPanel
  }

  private def buildInitialEnvCombo(): JComboBox[String] = {
    val model = new DefaultComboBoxModel[String](InitialEnvironment.values.map(_.displayName))
    val combo = new JComboBox[String](model)
    combo.setSelectedIndex(simulator.getInitialEnvironment.ordinal)
    combo.setToolTipText("Select the initial ground and water shape.")
    combo
  }

  override protected def ok(): Unit = {
    super.ok()
    val selected = InitialEnvironment.fromOrdinal(initialEnvCombo.getSelectedIndex)
    simulator.setInitialEnvironment(selected)
  }
}
