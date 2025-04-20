// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid

import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.liquid.config.ConfigurationEnum

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import scala.compiletime.uninitialized


/**
  * Use this modal dialog to let the user choose from among the
  * different game options.
  * @author Bary Becker
  */
class LiquidOptionsDialog private[liquid](parent: Component, simulator: LiquidSimulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  private var configurationChoiceField: JComboBox[String] = uninitialized
  private var showGridCheckbox: JCheckBox = uninitialized

  override protected def addAdditionalToggles(togglesPanel: JPanel): Unit = {
    val sim = getSimulator.asInstanceOf[LiquidSimulator]
    val ropts = sim.getRenderingOptions
    showGridCheckbox = createCheckBox(
      "Show Wireframe", "draw showing the underlying wireframe mesh",
      ropts.getShowGrid)
    togglesPanel.add(showGridCheckbox)
  }

  override protected def createCustomParamPanel: JPanel = {
    val customParamPanel = new JPanel
    customParamPanel.setLayout(new BorderLayout)
    val liquidParamPanel = new JPanel
    liquidParamPanel.setLayout(new BoxLayout(liquidParamPanel, BoxLayout.Y_AXIS))
    liquidParamPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Liquid Parameters"))
    configurationChoiceField = createConfigChoice
    liquidParamPanel.add(configurationChoiceField)
    customParamPanel.add(liquidParamPanel, BorderLayout.NORTH)
    customParamPanel
  }

  private def createConfigChoice = {
    val configurationChoice = new JComboBox[String]
    configurationChoice.setModel(new DefaultComboBoxModel[String](ConfigurationEnum.values.map(_.toString)))
    configurationChoice.setToolTipText(ConfigurationEnum.values(0).description)
    configurationChoice.addActionListener(this)
    configurationChoice.setSelectedItem(ConfigurationEnum.DEFAULT_VALUE)
    configurationChoice
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    val source = e.getSource
    if (source eq configurationChoiceField) {
      val selectedValue = ConfigurationEnum.toEnum(configurationChoiceField.getSelectedItem.toString)
      configurationChoiceField.setToolTipText(selectedValue.description)
    }
  }

  override protected def ok(): Unit = { // set the liquid environment
    val simulator = getSimulator.asInstanceOf[LiquidSimulator]
    val selected = ConfigurationEnum.toEnum(configurationChoiceField.getSelectedItem.toString)
    simulator.loadEnvironment(selected.fileName)
    simulator.getRenderingOptions.setShowGrid(showGridCheckbox.isSelected)
    super.ok()
  }
}