// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.options

import com.barrybecker4.simulation.habitat.HabitatSimulator
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener}

import javax.swing.*
import java.awt.*
import javax.swing.event.{ChangeEvent, ChangeListener}
import scala.collection.mutable.ArrayBuffer


/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
class DynamicOptions(val simulator: HabitatSimulator)
  extends JPanel with SliderGroupChangeListener with ChangeListener {

  private val tabbedPane = new JTabbedPane()
  private var numPixelsPerPointSlider: JSlider = _
  setLayout(new BorderLayout())
  setPreferredSize(new Dimension(300, 300))

  private val sliderGroups = createSliderGroups()

  add(tabbedPane, BorderLayout.CENTER)
  add(createPixelsPerXPointSlider(), BorderLayout.SOUTH)

  def update(): Unit = {}
  def reset(): Unit = { for (group <- sliderGroups) group.reset() }

  private def hexColor(color: Color): String =
    "#" + Integer.toHexString(color.getRGB).substring(2)


  private def createPixelsPerXPointSlider(): JPanel = {
    numPixelsPerPointSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 5)
    numPixelsPerPointSlider.addChangeListener(this)

    val panel = new JPanel(new BorderLayout())
    panel.add(new JLabel("Pixels per X point"), BorderLayout.NORTH)
    panel.add(numPixelsPerPointSlider, BorderLayout.CENTER)
    panel
  }

  private def createSliderGroups(): ArrayBuffer[CreatureSliderGroup] = {
    val sliderGroups = ArrayBuffer[CreatureSliderGroup]()

    for (creaturePop <- simulator.getPopulations) {
      val group = new CreatureSliderGroup(creaturePop)
      group.setSliderListener(this)
      sliderGroups.append(group)

      val groupPanel = new JPanel(new BorderLayout())
      groupPanel.add(group, BorderLayout.NORTH);

      val creatureType = creaturePop.creatureType
      val color = hexColor(creatureType.color)
      tabbedPane.add(s"<html>${creatureType.name} <font color='$color'>&#9679;</font></html>", groupPanel)
    }
    sliderGroups
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    for (group <- sliderGroups) {
      group.checkSliderChanged(sliderName, value)
    }
  }

  override def stateChanged(e: ChangeEvent): Unit = {
    val source = e.getSource
    if (source == numPixelsPerPointSlider) {
      simulator.setNumPixelsPerXPoint(numPixelsPerPointSlider.getValue)
    }
  }
}
