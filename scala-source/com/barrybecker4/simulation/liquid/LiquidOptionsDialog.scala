// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid

import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.liquid.config.ConfigurationEnum
import javax.swing._
import java.awt._
import java.awt.event.ActionEvent


/**
  * Use this modal dialog to let the user choose from among the
  * different game options.
  * @author Bary Becker
  */
class LiquidOptionsDialog private[liquid](parent: Component, simulator: LiquidSimulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  private var configurationChoiceField: JComboBox[String] = _
  private var showPressureCheckbox: JCheckBox = _
  private var showCellStatusCheckbox: JCheckBox = _
  private var showGridCheckbox: JCheckBox = _
  private var showVelocitiesCheckbox: JCheckBox = _
  private var useSingleStepModeCheckbox: JCheckBox = _

  override protected def addAdditionalToggles(togglesPanel: JPanel) {
    val sim = getSimulator.asInstanceOf[LiquidSimulator]
    val ropts = sim.getRenderingOptions
    showGridCheckbox = createCheckBox(
      "Show Wireframe", "draw showing the underlying wireframe mesh",
      ropts.getShowGrid)
    showVelocitiesCheckbox = createCheckBox(
      "Show Velocity Vectors", "show lines representing velocity vectors on each partical mass",
      ropts.getShowVelocities)
    showPressureCheckbox = createCheckBox(
      "Show Force Vectors", "show lines representing force vectors on each partical mass",
      ropts.getShowPressures)
    showCellStatusCheckbox = createCheckBox(
      "Show Cell Status", "show status for each of the cells",
      ropts.getShowCellStatus)
    useSingleStepModeCheckbox = createCheckBox(
      "Use Single Stepping", "For debugging is may be useful to press a key to advance each timestep",
      sim.getSingleStepMode)

    togglesPanel.add(showGridCheckbox)
    togglesPanel.add(showVelocitiesCheckbox)
    togglesPanel.add(showPressureCheckbox)
    togglesPanel.add(showCellStatusCheckbox)
    togglesPanel.add(useSingleStepModeCheckbox)
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
    simulator.getRenderingOptions.setShowVelocities(showVelocitiesCheckbox.isSelected)
    simulator.getRenderingOptions.setShowPressures(showPressureCheckbox.isSelected)
    simulator.getRenderingOptions.setShowCellStatus(showCellStatusCheckbox.isSelected)
    simulator.setSingleStepMode(useSingleStepModeCheckbox.isSelected)
    super.ok()
  }
}