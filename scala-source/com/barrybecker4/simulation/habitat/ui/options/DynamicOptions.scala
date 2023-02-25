// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.options

import com.barrybecker4.simulation.habitat.HabitatSimulator
import com.barrybecker4.simulation.habitat.ui.options.DynamicOptions.*
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener}
import com.barrybecker4.simulation.habitat.creatures.populations.Populations.{POPULATIONS, DEFAULT_POPULATIONS_INDEX}

import javax.swing.*
import java.awt.*
import java.awt.event.{ItemEvent, ItemListener}
import javax.swing.border.EtchedBorder
import javax.swing.event.{ChangeEvent, ChangeListener}
import scala.collection.mutable.ArrayBuffer

object DynamicOptions {
  val INITIAL_ITERATIONS_PER_FRAME = 1
  private val INITIAL_X_PIXELS_PER_POINT = 5
}

/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
class DynamicOptions(val simulator: HabitatSimulator)
  extends JPanel with SliderGroupChangeListener with ChangeListener with ItemListener {

  private val tabbedPane = new JTabbedPane()
  private var numPixelsPerPointSlider: JSlider = _
  private var iterationsPerFrameSlider: JSlider = _

  private var sliderGroups = createSliderGroups()
  private val topControlPanel = createTopControlPanel()
  private val bottomSlidersPanel = createBottomSlidersPanel()
  private var populationsChoice = new JComboBox[String]

  initialize()

  def update(): Unit = {}

  def reset(): Unit = {
    for (group <- sliderGroups) group.reset()
  }

  private def initialize(): Unit = {
    setLayout(new BorderLayout())
    setPreferredSize(new Dimension(300, 300))

    add(topControlPanel, BorderLayout.NORTH)
    add(tabbedPane, BorderLayout.CENTER)
    add(bottomSlidersPanel, BorderLayout.SOUTH)
  }

  private def hexColor(color: Color): String =
    "#" + Integer.toHexString(color.getRGB).substring(2)

  private def createTopControlPanel(): JPanel = {
    val panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED))


    val populationsChoicePanel: JPanel = new JPanel
    val label: JLabel = new JLabel("Select a habitat: ")
    populationsChoice = new JComboBox[String]
    for (populationsType <- POPULATIONS) {
      populationsChoice.addItem(populationsType.getName) // .name
    }

    populationsChoice.setSelectedIndex(DEFAULT_POPULATIONS_INDEX)
    populationsChoice.addItemListener(this)
    populationsChoicePanel.add(label)
    populationsChoicePanel.add(populationsChoice)
    populationsChoicePanel

    panel.add(populationsChoicePanel)
    panel
  }

  private def createBottomSlidersPanel(): JPanel = {
    val panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED))

    panel.add(createPixelsPerXPointSlider())
    panel.add(createIterationsPerFrameSlider())
    panel
  }

  private def createPixelsPerXPointSlider(): JPanel = {
    numPixelsPerPointSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, INITIAL_X_PIXELS_PER_POINT)
    numPixelsPerPointSlider.addChangeListener(this)

    val panel = new JPanel(new BorderLayout())
    panel.add(new JLabel("Pixels per X point"), BorderLayout.NORTH)
    panel.add(numPixelsPerPointSlider, BorderLayout.CENTER)
    panel
  }

  private def createIterationsPerFrameSlider(): JPanel = {
    iterationsPerFrameSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 50, INITIAL_ITERATIONS_PER_FRAME)
    iterationsPerFrameSlider.addChangeListener(this)

    val panel = new JPanel(new BorderLayout())
    panel.add(new JLabel("Iterations per Frame"), BorderLayout.NORTH)
    panel.add(iterationsPerFrameSlider, BorderLayout.CENTER)
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
    else if (source == iterationsPerFrameSlider) {
      simulator.setIterationsPerFrame(iterationsPerFrameSlider.getValue)
    }
    else throw new IllegalArgumentException("Unexpected source: " + source);
  }

  override def itemStateChanged(e: ItemEvent): Unit = {
    val populationsType = POPULATIONS.filter(p => p.getName == e.getItem.toString).head
    // set the new pop type on the sim
    // this.changePopulation // update panel
  }

}
