// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.ui

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.dungeon.ui.DynamicOptions
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}
import com.barrybecker4.simulation.dungeon.ui.DungeonOptionsChangedListener
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}

import java.awt.*
import java.awt.event.*
import javax.swing.*


/**
 * Dynamic controls for the Dungeon generator that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
object DynamicOptions {
  private val HALLWAY_WIDTH: Int = 1
  private val PREFERRED_WIDTH = 300
  private val SPACING = 14
}

class DynamicOptions(listener: DungeonOptionsChangedListener)
  extends JPanel with SliderGroupChangeListener with ActionListener {

  var dungeonOptions: DungeonOptions = DungeonOptions()
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(DynamicOptions.PREFERRED_WIDTH, 900))
  val generalPanel: JPanel = createGeneralControls

  add(createButtons)
  add(Box.createVerticalStrut(DynamicOptions.SPACING))
  add(generalPanel)
  add(Box.createVerticalStrut(DynamicOptions.SPACING))

  val fill = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)
  private var resetButton: JButton = _
  private var parameterSliders: ParameterSliders = _
  private var halfPaddedCheckBox: JCheckBox = _
  private var showGridCHeckBox: JCheckBox = _

  private def createGeneralControls = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("General parameters"))

    parameterSliders = ParameterSliders()
    parameterSliders.addSliderChangeListener(this)
    panel.add(parameterSliders, BorderLayout.CENTER)
    panel.add(createCheckboxPanel, BorderLayout.SOUTH)
    panel
  }

  private def createTitledBorder(title: String) =
    BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder(title),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    )

  private def createButtons = {
    val buttonsPanel = new JPanel

    resetButton = new JButton(AppContext.getLabel("RESET"))
    resetButton.setToolTipText("Restore the initial configuration")
    resetButton.addActionListener(this)

    buttonsPanel.add(resetButton)
    buttonsPanel
  }

  private def createCheckboxPanel = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))

    halfPaddedCheckBox = new JCheckBox()
    val halfPaddedPanel = createCheckBox("Use only half the padding ",
      "If checked, padding only goes on on the top left instead of all around",
      dungeonOptions.halfPadded, halfPaddedCheckBox)

    showGridCHeckBox = new JCheckBox()
    val showGridPanel = createCheckBox("Show a grid if checked ",
      "When checked, a grid shows on the background",
      dungeonOptions.showGrid, showGridCHeckBox)

    panel.add(Box.createVerticalStrut(10))
    panel.add(halfPaddedPanel)
    panel.add(showGridPanel)
    panel.add(Box.createHorizontalGlue)
    panel
  }

  private def createCheckBox(label: String, ttip: String, initialValue: Boolean, checkBox: JCheckBox): JPanel = {
    val checkboxPanel = new JPanel(new BorderLayout())

    checkboxPanel.setToolTipText(ttip)
    val cbLabel = new JLabel(label)
    checkBox.setSelected(initialValue)
    checkBox.addActionListener(this)

    checkboxPanel.add(cbLabel, BorderLayout.CENTER)
    checkboxPanel.add(checkBox, BorderLayout.EAST)
    checkboxPanel
  }

  def reset(): Unit = parameterSliders.reset()

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    dungeonOptions = parameterSliders.onSliderChanged(sliderIndex, sliderName, value, dungeonOptions, this)
    listener.optionsChanged(dungeonOptions)
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == halfPaddedCheckBox)
      dungeonOptions = dungeonOptions.setHalfPadded(halfPaddedCheckBox.isSelected)
    else if (e.getSource == showGridCHeckBox)
      dungeonOptions = dungeonOptions.setShowGrid(showGridCHeckBox.isSelected)
    else throw new IllegalArgumentException()
    listener.optionsChanged(dungeonOptions)
  }
}
