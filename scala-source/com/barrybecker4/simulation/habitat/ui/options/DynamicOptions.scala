// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.options

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.simulation.habitat.HabitatSimulator
import com.barrybecker4.simulation.habitat.ui.options.DynamicOptions.*
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener}
import com.barrybecker4.simulation.habitat.creatures.populations.Habitat.{DEFAULT_HABITAT_INDEX, HABITATS}

import javax.swing.*
import java.awt.*
import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import javax.swing.border.EtchedBorder
import javax.swing.event.{ChangeEvent, ChangeListener}
import scala.collection.mutable.ArrayBuffer

object DynamicOptions {
  val INITIAL_ITERATIONS_PER_FRAME = 1
  val DEFAULT_USE_CONTINUOUS_ITERATION = true
  val DEFAULT_DEBUG = false
  private val INITIAL_X_PIXELS_PER_POINT = 5
}

/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
class DynamicOptions(val simulator: HabitatSimulator)
  extends JPanel with SliderGroupChangeListener with ChangeListener
                 with ItemListener with ActionListener {

  private val tabbedPane = new JTabbedPane()
  private var numPixelsPerPointSlider: JSlider = _
  private var iterationsPerFrameSlider: JSlider = _
  private var debugCheckBox: JCheckBox = _

  private var useContinuousIteration: JCheckBox = _
  private var nextButton: JButton = _
  private val sliderGroups = createSliderGroups()
  private val topControlPanel = createTopControlPanel()
  private val bottomSlidersPanel = createBottomSlidersPanel()
  private var habitatChoice: JComboBox[String] = _

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

    debugCheckBox = createCheckbox("Debug", DEFAULT_DEBUG,
      "When checked, debug info is shown graphicall and in the console.")

    panel.add(createHabitatChoicePanel())
    panel.add(createIncrementPanel())
    panel.add(debugCheckBox)
    panel
  }

  private def createHabitatChoicePanel(): JPanel = {
    val panel: JPanel = new JPanel()
    val choiceLabel: JLabel = new JLabel("Select a habitat: ")
    habitatChoice = new JComboBox[String]
    for (habitat <- HABITATS) {
      habitatChoice.addItem(habitat.getName)
    }

    habitatChoice.setSelectedIndex(DEFAULT_HABITAT_INDEX)
    habitatChoice.addItemListener(this)

    panel.add(choiceLabel)
    panel.add(habitatChoice)
    panel
  }

  /** Controls to allow iterating continuously or one step at a time.
   */
  private def createIncrementPanel(): JPanel = {
    val panel: JPanel = new JPanel(new BorderLayout)
    useContinuousIteration =
      createCheckbox("Continuous iteration", DEFAULT_USE_CONTINUOUS_ITERATION,
        "When checked, the simulation proceeds continuously. " +
          "When unchecked, use the 'Next' button to advance one time step at a time.")

    val nextPanel = new JPanel()
    nextPanel.setToolTipText("Click to advance one time step at a time")
    nextPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20))
    nextButton = new JButton("Next")
    nextButton.addActionListener(this)
    nextButton.setEnabled(!useContinuousIteration.isSelected)
    nextPanel.add(nextButton)

    panel.add(useContinuousIteration, BorderLayout.CENTER)
    panel.add(nextPanel, BorderLayout.EAST)
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

  private def createCheckbox(labelText: String, defaultValue: Boolean, tooltip: String): JCheckBox = {
    val cb: JCheckBox = new JCheckBox(labelText)
    cb.setToolTipText(tooltip)
    cb.setSelected(defaultValue)
    cb.addActionListener(this)
    cb
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

    for (creaturePop <- simulator.getHabitat) {
      val group = new CreatureSliderGroup(creaturePop)
      group.setSliderListener(this)
      sliderGroups.append(group)

      val groupPanel = new JPanel(new BorderLayout())
      groupPanel.add(group, BorderLayout.NORTH)

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
    else throw new IllegalArgumentException("Unexpected source: " + source)
  }

  override def itemStateChanged(e: ItemEvent): Unit = {
    val habitat = HABITATS.filter(p => p.getName == e.getItem.toString).head
    habitat.reset()
    simulator.setHabitat(habitat)
    tabbedPane.removeAll()
    createSliderGroups()
    repaint()
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    val source: Component = e.getSource.asInstanceOf[Component]
    if (source == nextButton) {
      simulator.requestNextStep()
    }
    else if (source == useContinuousIteration) {
        val useContinuous: Boolean = useContinuousIteration.isSelected
        simulator.setUseContinuousIteration(useContinuous)
        nextButton.setEnabled(!useContinuous)
        if (!useContinuous) {
          // do one last step in case the rendering was interrupted.
          ThreadUtil.sleep(100)
          simulator.requestNextStep()
        }
    }
    else if (source == debugCheckBox) {
      val isDebug = debugCheckBox.isSelected
      simulator.setDebug(isDebug)
    }
  }
}
