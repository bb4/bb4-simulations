// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.simulation.conway.model.ConwayModel
import com.barrybecker4.simulation.conway.model.ConwayProcessor
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}
import java.awt.{BorderLayout, Dimension}
import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import javax.swing._
import javax.swing.border.Border
import scala.compiletime.uninitialized

/**
  * Dynamic controls for the Conway simulation shown on the right.
  *
  * @author Barry Becker
  */
object DynamicOptions {
  private val NUM_STEPS_PER_FRAME_SLIDER = "Num steps per frame"
  private val SCALE_SLIDER = "Scale"
  private val PREFERRED_WIDTH = 240
  private val SPACING = 14
  private val GENERAL_SLIDER_PROPS = Array(
    new SliderProperties(NUM_STEPS_PER_FRAME_SLIDER, 1, 20, ConwayModel.DEFAULT_NUM_STEPS_PER_FRAME),
    new SliderProperties(SCALE_SLIDER, 1, 20, ConwayModel.DEFAULT_SCALE_FACTOR)
  )
}

class DynamicOptions(var conwayModel: ConwayModel, var simulator: ConwayExplorer)
  extends JPanel with SliderGroupChangeListener with ActionListener with ItemListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(DynamicOptions.PREFERRED_WIDTH, 800))
  val generalPanel: JPanel = createGeneralControls
  val brushPanel: JPanel = createBrushControls
  val legend: ContinuousColorLegend = new ContinuousColorLegend(null, conwayModel.getColormap, true)

  add(createIncrementPanel)
  add(createButtons)
  add(createRuleDropdown)
  add(legend)
  add(Box.createVerticalStrut(DynamicOptions.SPACING))
  add(generalPanel)
  add(Box.createVerticalStrut(DynamicOptions.SPACING))
  add(brushPanel)

  val fill: JPanel = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)

  private var nextButton: JButton = uninitialized
  private var ruleChoice: JComboBox[String] = uninitialized
  private var generalSliderGroup: SliderGroup = uninitialized
  private var useContinuousIteration: JCheckBox = uninitialized
  private var useParallelComputation: JCheckBox = uninitialized
  private var showShadowsCheckbox: JCheckBox = uninitialized
  private var wrapCheckbox: JCheckBox = uninitialized

  private def createGeneralControls: JPanel = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("General parameters"))
    generalSliderGroup = new SliderGroup(DynamicOptions.GENERAL_SLIDER_PROPS)
    generalSliderGroup.setSliderListener(this)
    panel.add(generalSliderGroup, BorderLayout.CENTER)
    panel
  }

  private def createTitledBorder(title: String): Border =
    BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder(title),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    )

  private def createBrushControls: JPanel = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("Brush parameters (left: raise; right: lower)"))
    panel
  }

  private def createIncrementPanel: JPanel = {
    val panel = new JPanel(new BorderLayout)
    useContinuousIteration =
      createCheckbox(
        "Continuous iteration",
        ConwayModel.DEFAULT_USE_CONTINUOUS_ITERATION,
        "When checked, the simulation proceeds continuously. " +
          "When unchecked, use the 'Next' button to advance one time step at a time."
      )

    val nextPanel = new JPanel()
    nextPanel.setToolTipText("Click to advance one time step at a time")
    nextPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20))
    nextButton = new JButton("Next")
    nextButton.addActionListener(this)
    nextButton.setEnabled(!useContinuousIteration.isSelected)
    nextPanel.add(nextButton)

    panel.add(useContinuousIteration, BorderLayout.CENTER)
    panel.add(nextPanel, BorderLayout.EAST)
    panel.add(createCheckboxPanel, BorderLayout.SOUTH)
    panel
  }

  private def createCheckboxPanel: JPanel = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    useParallelComputation = createCheckbox(
      "Parallel computation",
      ConwayProcessor.DEFAULT_USE_PARALLEL,
      "When checked, computation is done in parallel using all available processors."
    )
    showShadowsCheckbox = createCheckbox(
      "Show shadows",
      ConwayModel.DEFAULT_SHOW_SHADOWS,
      "When checked, you will see trails of where life has been."
    )
    wrapCheckbox = createCheckbox(
      "Wrap grid",
      ConwayModel.DEFAULT_WRAP_GRID,
      "When checked, any life that expands beyond a border will wrap to the other edge."
    )
    panel.add(useParallelComputation)
    panel.add(showShadowsCheckbox)
    panel.add(wrapCheckbox)
    panel
  }

  private def createCheckbox(labelText: String, defaultValue: Boolean, tooltip: String): JCheckBox = {
    val cb = new JCheckBox(labelText)
    cb.setToolTipText(tooltip)
    cb.setSelected(defaultValue)
    cb.addActionListener(this)
    cb
  }

  private def createRuleDropdown: JPanel = {
    val ruleChoicePanel = new JPanel
    val label = new JLabel("Rule to apply: ")
    ruleChoice = new JComboBox[String]
    for ruleType <- ConwayProcessor.RuleType.values do ruleChoice.addItem(ruleType.toString)
    ruleChoice.setSelectedIndex(ConwayProcessor.DEFAULT_RULE_TYPE.ordinal)
    ruleChoice.addItemListener(this)
    ruleChoicePanel.add(label)
    ruleChoicePanel.add(ruleChoice)
    ruleChoicePanel
  }

  private def createButtons: JPanel = new JPanel

  def reset(): Unit = generalSliderGroup.reset()

  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit =
    sliderName match {
      case DynamicOptions.NUM_STEPS_PER_FRAME_SLIDER => conwayModel.setNumStepsPerFrame(value.toInt)
      case DynamicOptions.SCALE_SLIDER =>
        conwayModel.setScale(value.toInt)
        simulator.getInteractionHandler.setScale(value)
      case _ => throw IllegalArgumentException("Unexpected slider: " + sliderName)
    }

  override def itemStateChanged(e: ItemEvent): Unit =
    if e.getStateChange == ItemEvent.SELECTED then
      val ruleType =
        ConwayProcessor.RuleType.valueOf(ruleChoice.getSelectedItem.asInstanceOf[String])
      conwayModel.setRuleType(ruleType)

  override def actionPerformed(e: ActionEvent): Unit = {
    val src = e.getSource
    if src eq nextButton then conwayModel.requestNextStep()
    else if src eq useContinuousIteration then onContinuousIterationToggled()
    else if src eq useParallelComputation then
      conwayModel.setUseParallelComputation(useParallelComputation.isSelected)
    else if src eq wrapCheckbox then
      conwayModel.setWrapGrid(wrapCheckbox.isSelected)
      conwayModel.requestRestart()
    else if src eq showShadowsCheckbox then
      conwayModel.setShowShadows(showShadowsCheckbox.isSelected)
      conwayModel.requestRestart()
    else throw IllegalStateException("Unexpected control: " + src)
  }

  private def onContinuousIterationToggled(): Unit = {
    val useContinuous = useContinuousIteration.isSelected
    conwayModel.setUseContinuousIteration(useContinuous)
    nextButton.setEnabled(!useContinuous)
    if !useContinuous then
      ThreadUtil.sleep(100)
      conwayModel.requestNextStep()
  }
}
