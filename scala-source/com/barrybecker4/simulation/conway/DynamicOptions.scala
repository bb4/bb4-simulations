// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway

import com.barrybecker4.simulation.conway.model.ConwayModel
import com.barrybecker4.simulation.conway.model.ConwayProcessor
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties
import javax.swing._
import javax.swing.border.Border
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import com.barrybecker4.common.concurrency.ThreadUtil



/**
  * Dynamic controls for the Conway simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
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

  setLayout (new BoxLayout (this, BoxLayout.Y_AXIS) )
  setBorder (BorderFactory.createEtchedBorder)
  setPreferredSize (new Dimension (DynamicOptions.PREFERRED_WIDTH, 800) )
  val generalPanel: JPanel = createGeneralControls
  val brushPanel: JPanel = createBrushControls
  val legend: ContinuousColorLegend = new ContinuousColorLegend (null, conwayModel.getColormap, true)

  add(createIncrementPanel)
  add(createButtons)
  add(createRuleDropdown)
  add(legend)
  add(Box.createVerticalStrut (DynamicOptions.SPACING) )
  add(generalPanel)
  add(Box.createVerticalStrut (DynamicOptions.SPACING) )
  add(brushPanel)

  val fill: JPanel = new JPanel
  fill.setPreferredSize (new Dimension (1, 1000) )
  add (fill)
  private var nextButton: JButton = _
  private var ruleChoice: JComboBox[String] = _
  private var generalSliderGroup: SliderGroup = _
  private var useContinuousIteration: JCheckBox = _
  private var useParallelComputation: JCheckBox = _
  private var showShadowsCheckbox: JCheckBox = _
  private var wrapCheckbox: JCheckBox = _

  private def createGeneralControls: JPanel = {
    val panel: JPanel = new JPanel (new BorderLayout)
    panel.setBorder (createTitledBorder ("General parameters") )
    generalSliderGroup = new SliderGroup (DynamicOptions.GENERAL_SLIDER_PROPS)
    generalSliderGroup.setSliderListener (this)
    panel.add (generalSliderGroup, BorderLayout.CENTER)
    panel
  }

  private def createTitledBorder (title: String): Border = {
    BorderFactory.createCompoundBorder (BorderFactory.createTitledBorder (title), BorderFactory.createEmptyBorder (5, 5, 5, 5) )
  }

  private def createBrushControls: JPanel = {
    val panel: JPanel = new JPanel (new BorderLayout)
    panel.setBorder (createTitledBorder ("Brush Parameters (left: raise; right: lower)") )
    panel
  }

  /**
    * Controls to allow iterating continuously or one step at a time.
    */
  private def createIncrementPanel: JPanel = {
    val panel: JPanel = new JPanel (new BorderLayout)
    useContinuousIteration =
      createCheckbox("Continuous iteration", ConwayModel.DEFAULT_USE_CONTINUOUS_ITERATION,
        "When checked, the simulation proceeds continuously. " +
          "When unchecked, use the 'Next' button to advance one time step at a time.")

    val nextPanel = new JPanel()
    nextPanel.setToolTipText("Click to advance one time step at a time")
    nextPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20))
    nextButton = new JButton ("Next")
    nextButton.addActionListener(this)
    nextButton.setEnabled (!useContinuousIteration.isSelected )
    nextPanel.add(nextButton)

    panel.add(useContinuousIteration, BorderLayout.CENTER)
    panel.add(nextPanel, BorderLayout.EAST)
    panel.add(createCheckboxPanel, BorderLayout.SOUTH)
    panel
  }

  /** @return checkbox options. */
  private def createCheckboxPanel: JPanel = {
    val panel: JPanel = new JPanel
    panel.setLayout(new BoxLayout (panel, BoxLayout.Y_AXIS) )
    useParallelComputation = createCheckbox("Parallel computation", ConwayProcessor.DEFAULT_USE_PARALLEL,
      "When checked, computation is don in parallel, using all available processors.")
    showShadowsCheckbox = createCheckbox ("Show shadows", ConwayModel.DEFAULT_SHOW_SHADOWS,
      "When checked, you will see trails of where life has been.")
    wrapCheckbox = createCheckbox ("Wrap grid", ConwayModel.DEFAULT_WRAP_GRID,
      "When checked, any life that expands beyond a border will wrap to the other edge.")
    panel.add(useParallelComputation)
    panel.add(showShadowsCheckbox)
    panel.add(wrapCheckbox)
    panel
  }

  private def createCheckbox(labelText: String, defaultValue: Boolean, tooltip: String): JCheckBox = {
    val cb: JCheckBox = new JCheckBox(labelText)
    cb.setToolTipText(tooltip)
    cb.setSelected (defaultValue)
    cb.addActionListener(this)
    cb
  }

  /**
    * The dropdown menu for selecting a rule type.
    *
    * @return a dropdown/down component.
    */
  private def createRuleDropdown: JPanel = {
    val ruleChoicePanel: JPanel = new JPanel
    val label: JLabel = new JLabel("Rule to apply: ")
    ruleChoice = new JComboBox[String]
    for (ruleType <- ConwayProcessor.RuleType.values) {
      ruleChoice.addItem(ruleType.toString) // .name
    }
    ruleChoice.setSelectedIndex(ConwayProcessor.DEFAULT_RULE_TYPE.ordinal)
    ruleChoice.addItemListener(this)
    ruleChoicePanel.add(label)
    ruleChoicePanel.add(ruleChoice)
    ruleChoicePanel
  }

  private def createButtons: JPanel = {
    val buttonsPanel: JPanel = new JPanel
    //resetButton = new JButton(AppContext.getLabel("RESET"));
    //resetButton.addActionListener(this);
    //buttonsPanel.add(resetButton);
    buttonsPanel
  }
  def reset(): Unit = { generalSliderGroup.reset() }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    sliderName match {
      case DynamicOptions.NUM_STEPS_PER_FRAME_SLIDER => conwayModel.setNumStepsPerFrame(value.toInt)
      case DynamicOptions.SCALE_SLIDER =>
        conwayModel.setScale(value.toInt)
        simulator.getInteractionHandler.setScale(value)
      case _ => throw new IllegalArgumentException ("Unexpected slider: " + sliderName)
    }
  }

  override def itemStateChanged (e: ItemEvent): Unit = {
    val ruleType: ConwayProcessor.RuleType =
      ConwayProcessor.RuleType.valueOf(ruleChoice.getSelectedItem.toString)
    conwayModel.setRuleType (ruleType)
  }

  override def actionPerformed (e: ActionEvent): Unit = {
    val source: Component = e.getSource.asInstanceOf[Component]
    if (source == nextButton) {
      conwayModel.requestNextStep ()
    }
    else {
      if (source == useContinuousIteration) {
        val useContinuous: Boolean = useContinuousIteration.isSelected
        conwayModel.setUseContinuousIteration(useContinuous)
        nextButton.setEnabled (!useContinuous)
        if (!useContinuous) {
          // do one last step in case the rendering was interrupted.
          ThreadUtil.sleep (100)
          conwayModel.requestNextStep ()
        }
      }
      else {
        if (source == useParallelComputation) {
          conwayModel.setUseParallelComputation (useParallelComputation.isSelected)
        }
        else {
          if (source == wrapCheckbox) {
            conwayModel.setWrapGrid (wrapCheckbox.isSelected)
            conwayModel.requestRestart ()
          }
          else {
            if (source == showShadowsCheckbox) {
              conwayModel.setShowShadows(showShadowsCheckbox.isSelected)
              conwayModel.requestRestart()
            }
            else {
              throw new IllegalStateException ("Unexpected button " + e.getSource)
            }
          }
        }
      }
    }
  }

}
