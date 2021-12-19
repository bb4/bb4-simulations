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
  val MIN_WIDTH: Int = 3
  val MIN_HEIGHT: Int = 3
  
  private val MAX_ROOM_WIDTH_SLIDER = "Max Width"
  private val MAX_ROOM_HEIGHT_SLIDER = "Max Height"
  private val PERCENT_FILLED_SLIDER = "Percent Filled"
  private val WALL_BORDER_WIDTH_SLIDER = "Wall Border Thickness"
  
  private val PREFERRED_WIDTH = 300
  private val SPACING = 14

  private val GENERAL_SLIDER_PROPS = Array(
    new SliderProperties(MAX_ROOM_WIDTH_SLIDER, MIN_WIDTH, 30, DungeonOptions.DEFAULT_MAX_ROOM_WIDTH, 1),
    new SliderProperties(MAX_ROOM_HEIGHT_SLIDER, MIN_HEIGHT, 20.0, DungeonOptions.DEFAULT_MAX_ROOM_HEIGHT, 1),
    new SliderProperties(PERCENT_FILLED_SLIDER, 10, 100, DungeonOptions.DEFAULT_PERCENT_FILLED, 1),
    new SliderProperties(WALL_BORDER_WIDTH_SLIDER, 0, 4, DungeonOptions.DEFAULT_BORDER_WIDTH, 1),
  )
}

class DynamicOptions (listener: DungeonOptionsChangedListener)
  extends JPanel with SliderGroupChangeListener with ActionListener {

  var oldDungeonOptions: DungeonOptions = DungeonOptions()
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
  private var kernelChoice: JComboBox[String] = _
  private var resetButton: JButton = _
  private var generalSliderGroup: SliderGroup = _

  private def createGeneralControls = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("General parameters"))
    generalSliderGroup = new SliderGroup(DynamicOptions.GENERAL_SLIDER_PROPS)
    generalSliderGroup.addSliderChangeListener(this)
    panel.add(generalSliderGroup, BorderLayout.CENTER)
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

  def reset(): Unit = generalSliderGroup.reset()

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    val dungeonOptions = sliderName match {
      case DynamicOptions.MAX_ROOM_WIDTH_SLIDER => oldDungeonOptions.setMaxRoomWidth(value.toInt)
      case DynamicOptions.MAX_ROOM_HEIGHT_SLIDER => oldDungeonOptions.setMaxRoomHeight(value.toInt)
      case DynamicOptions.PERCENT_FILLED_SLIDER => oldDungeonOptions.setPercentFilled(value.toInt)
      case DynamicOptions.WALL_BORDER_WIDTH_SLIDER => oldDungeonOptions.setWallBorderWidth(value.toInt)
      case _ => throw new IllegalArgumentException("Unexpected slider: " + sliderName)
    }
    oldDungeonOptions = dungeonOptions

    listener.optionsChanged(dungeonOptions)
  }
  

  override def actionPerformed(e: ActionEvent): Unit = {
    //if (e.getSource == resetButton) simulator.requestRestart()
    //else throw new IllegalStateException("Unexpected button " + e.getSource)
  }
}
