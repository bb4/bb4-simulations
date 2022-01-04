package com.barrybecker4.simulation.dungeon.ui

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.dungeon.ui.DynamicOptions
import com.barrybecker4.simulation.dungeon.model.DungeonModel
import com.barrybecker4.simulation.dungeon.ui.DungeonOptionsChangedListener
import com.barrybecker4.simulation.dungeon.generator.bsp.BspDungeonGenerator
import com.barrybecker4.simulation.dungeon.generator.uniongraph.UnionGraphDungeonGenerator
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}
import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy.GeneratorStrategyType
import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy.GeneratorStrategyType.*
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions

import java.awt.*
import java.awt.event.*
import javax.swing.*


/** 
 * Dynamic controls for the Dungeon generator that will show on the right.
 * They change the behavior of the simulation while it is running.
 */
class DynamicOptions(listener: DungeonOptionsChangedListener)
  extends JPanel with SliderGroupChangeListener with ActionListener with ItemListener {

  var dungeonOptions: DungeonOptions = DungeonOptions()
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  val generalPanel: JPanel = createGeneralControls
  private val SPACING = 14

  add(createButtons)
  add(Box.createVerticalStrut(SPACING))
  add(generalPanel)
  add(Box.createVerticalStrut(SPACING))

  private var resetButton: JButton = _
  private var parameterSliders: ParameterSliders = _
  private var halfPaddedCheckBox: JCheckBox = _
  private var showGridCHeckBox: JCheckBox = _

  private def createGeneralControls = {
    val panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    panel.setBorder(createTitledBorder("General parameters"))

    parameterSliders = ParameterSliders()
    parameterSliders.addSliderChangeListener(this)
    panel.add(GeneratorChoice(this))
    panel.add(parameterSliders)
    panel.add(createCheckboxPanel)

    val fill = new JPanel
    fill.setPreferredSize(new Dimension(1, 1200))
    panel.add(fill)

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
    val halfPaddedPanel = CheckBoxCreator.createCheckBox("Use only half the padding ",
      "If checked, padding only goes on on the top left instead of all around",
      dungeonOptions.halfPadded, halfPaddedCheckBox, this)

    showGridCHeckBox = new JCheckBox()
    val showGridPanel = CheckBoxCreator.createCheckBox("Show grid",
      "When checked, a grid shows on the background",
      dungeonOptions.showGrid, showGridCHeckBox, this)

    panel.add(Box.createVerticalStrut(10))
    panel.add(halfPaddedPanel)
    panel.add(showGridPanel)
    panel.add(Box.createHorizontalGlue)
    panel
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
    else if (e.getSource == resetButton) reset()
    else throw new IllegalArgumentException()
    listener.optionsChanged(dungeonOptions)
  }

  override def itemStateChanged(e: ItemEvent): Unit = {
    val strategyType: GeneratorStrategyType =
      GeneratorStrategyType.valueOf(e.getItem.toString)

    dungeonOptions = strategyType match {
      case GeneratorStrategyType.BinarySpacePartition => dungeonOptions.setGenerator(BspDungeonGenerator())
      case GeneratorStrategyType.UnionGraph => dungeonOptions.setGenerator(UnionGraphDungeonGenerator())
    }
    listener.optionsChanged(dungeonOptions)
  }
}
