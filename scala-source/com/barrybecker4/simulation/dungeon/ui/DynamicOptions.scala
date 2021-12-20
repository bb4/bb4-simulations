// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.ui

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.simulation.cave.CaveExplorer
import com.barrybecker4.simulation.cave.model.{CaveModel, CaveProcessor}
import com.barrybecker4.simulation.dungeon.ui.DynamicOptions
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}
import com.barrybecker4.simulation.dungeon.ui.DungeonOptionsChangedListener
import com.barrybecker4.ui.legend.ContinuousColorLegend
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

  val HALLWAY_WIDTH: Int = 1

  private val MAX_ROOM_WIDTH_SLIDER = "Max Width"
  private val MAX_ROOM_HEIGHT_SLIDER = "Max Height"
  private val PERCENT_FILLED_SLIDER = "Percent Filled"
  private val ROOM_PADDING_SLIDER = "Wall Border Thickness"
  private val CELL_SIZE_SLIDER = "Cell Size"

  private val PREFERRED_WIDTH = 300
  private val SPACING = 14

  private val GENERAL_SLIDER_PROPS = Array(
    new SliderProperties(MAX_ROOM_WIDTH_SLIDER, 2 * DungeonOptions.MIN_ROOM_DIM, 40, DungeonOptions.DEFAULT_MAX_ROOM_WIDTH, 1),
    new SliderProperties(MAX_ROOM_HEIGHT_SLIDER, 2 * DungeonOptions.MIN_ROOM_DIM, 40.0, DungeonOptions.DEFAULT_MAX_ROOM_HEIGHT, 1),
    new SliderProperties(PERCENT_FILLED_SLIDER, 10, 100, DungeonOptions.DEFAULT_PERCENT_FILLED, 1),
    new SliderProperties(ROOM_PADDING_SLIDER, 0, 4, DungeonOptions.DEFAULT_ROOM_PADDING, 1),
    new SliderProperties(CELL_SIZE_SLIDER, 1, 30, DungeonOptions.DEFAULT_CELL_SIZE, 1),
  )
}

class DynamicOptions (listener: DungeonOptionsChangedListener)
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
  private var generalSliderGroup: SliderGroup = _
  private var showGrid: JCheckBox = _

  private def createGeneralControls = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("General parameters"))
    generalSliderGroup = new SliderGroup(DynamicOptions.GENERAL_SLIDER_PROPS)
    generalSliderGroup.addSliderChangeListener(this)
    panel.add(generalSliderGroup, BorderLayout.CENTER)
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

    val showGridPanel = new JPanel
    showGridPanel.setToolTipText("When checked, a grid shows on the background. ")
    val showGridLabel = new JLabel("Show a grid if checked ")
    showGrid = new JCheckBox
    showGrid.setSelected(dungeonOptions.showGrid)
    showGrid.addActionListener(this)

    showGridPanel.add(showGridLabel)
    showGridPanel.add(showGrid)

    panel.add(showGridPanel)
    panel.add(Box.createHorizontalGlue)
    panel
  }

  def reset(): Unit = generalSliderGroup.reset()

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    dungeonOptions = sliderName match {
      case DynamicOptions.MAX_ROOM_WIDTH_SLIDER => dungeonOptions.setMaxRoomWidth(value.toInt)
      case DynamicOptions.MAX_ROOM_HEIGHT_SLIDER => dungeonOptions.setMaxRoomHeight(value.toInt)
      case DynamicOptions.PERCENT_FILLED_SLIDER => dungeonOptions.setPercentFilled(value.toInt)
      case DynamicOptions.ROOM_PADDING_SLIDER => dungeonOptions.setRoomPadding(value.toInt)
      case DynamicOptions.CELL_SIZE_SLIDER => dungeonOptions.setCellSize(value.toInt)
      case _ => throw new IllegalArgumentException("Unexpected slider: " + sliderName)
    }
    listener.optionsChanged(dungeonOptions)
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == showGrid) {
      dungeonOptions = dungeonOptions.setShowGrid(showGrid.isSelected)
      listener.optionsChanged(dungeonOptions)
    }
  }
}