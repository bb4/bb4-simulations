// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.components.NumberInput
import com.barrybecker4.ui.dialogs.OptionsDialog
import javax.swing._
import java.awt._
import java.awt.event.ActionEvent


object SimulatorOptionsDialog {
  private val MAX_NUM_STEPS_PER_FRAME = 10000
}

/**
  * The options get set directly on the simulator object that is passed in
  * @author Barry Becker
  */
abstract class SimulatorOptionsDialog(parent: Component,
                                      simulator: Simulator) extends OptionsDialog(parent) {
  // rendering option controls
  private var antialiasingCheckbox: JCheckBox = _
  private var recordAnimationCheckbox: JCheckBox = _
  // animation param options controls
  private var timeStepField: NumberInput = _
  private var numStepsPerFrameField: NumberInput = _
  private var scaleField: NumberInput = _
  // bottom buttons
  private val startButton = new GradientButton
  showContent()

  def getSimulator: Simulator = simulator

  override protected def createDialogContent: JComponent = {
    setResizable(true)
    val mainPanel = new JPanel
    mainPanel.setLayout(new BorderLayout)
    val buttonsPanel = createButtonsPanel
    val renderingParamPanel = createRenderingParamPanel
    val globalPhysicalParamPanel = createGlobalPhysicalParamPanel
    val customParamPanel = createCustomParamPanel
    val tabbedPanel = new JTabbedPane
    tabbedPanel.add("Rendering", renderingParamPanel)
    tabbedPanel.setToolTipTextAt(0, "Change the rendering options for the simulation")
    if (globalPhysicalParamPanel != null) {
      tabbedPanel.add("Animation", globalPhysicalParamPanel)
      tabbedPanel.setToolTipTextAt(0, "Change the animation and physical constants controlling the simulation")
    }
    tabbedPanel.add("Custom", customParamPanel)
    tabbedPanel.setToolTipTextAt(tabbedPanel.getTabCount - 1, "Change the custom options for the simulation")
    tabbedPanel.setSelectedComponent(customParamPanel)
    mainPanel.add(tabbedPanel, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)
    mainPanel
  }

  protected def createCheckBox(label: String, ttip: String, initialValue: Boolean): JCheckBox = {
    val cb = new JCheckBox(label, initialValue)
    cb.setToolTipText(ttip)
    cb.addActionListener(this)
    cb
  }

  override protected def createButtonsPanel: JPanel = {
    val buttonsPanel = new JPanel(new FlowLayout)
    initBottomButton(startButton, "Done", "Use these selections when running the simulation.")
    initBottomButton(cancelButton, "Cancel", "Resume the current simulation without changing the options")
    buttonsPanel.add(startButton)
    buttonsPanel.add(cancelButton)
    buttonsPanel
  }

  override def getTitle = "Simulation Configuration"

  protected def createRenderingParamPanel: JPanel = {
    val paramPanel = new JPanel
    paramPanel.setLayout(new BorderLayout)
    val togglesPanel = new JPanel
    togglesPanel.setLayout(new BoxLayout(togglesPanel, BoxLayout.Y_AXIS))
    togglesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Toggle Options"))
    val textInputsPanel = new JPanel
    textInputsPanel.setLayout(new BoxLayout(textInputsPanel, BoxLayout.Y_AXIS))
    textInputsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Animation Options"))
    antialiasingCheckbox = new JCheckBox("Use Antialiasing", simulator.getAntialiasing)
    antialiasingCheckbox.setToolTipText("this toggle the use of antialising when rendering lines.")
    antialiasingCheckbox.addActionListener(this)
    togglesPanel.add(antialiasingCheckbox)
    addAdditionalToggles(togglesPanel)
    recordAnimationCheckbox = new JCheckBox("Record Animation Frames", simulator.getRecordAnimation)
    recordAnimationCheckbox.setToolTipText("Record each animation frame to a unique file")
    recordAnimationCheckbox.addActionListener(this)
    togglesPanel.add(recordAnimationCheckbox)

    timeStepField = new NumberInput("Time Step (.001 slow - .9 fast but unstable):  ",
      simulator.getTimeStep,
      "This controls the size of the numerical integration steps", 0.001, 10.0, false)

    numStepsPerFrameField = new NumberInput("Num Steps Per Frame (1 slow but smooth - " +
      SimulatorOptionsDialog.MAX_NUM_STEPS_PER_FRAME + " (fast but choppy):  ",
      simulator.getNumStepsPerFrame,
      "This controls the number of the numerical intergration steps per animation frame", 1,
      SimulatorOptionsDialog.MAX_NUM_STEPS_PER_FRAME, true)

    textInputsPanel.add(timeStepField)
    textInputsPanel.add(numStepsPerFrameField)
    scaleField = new NumberInput("Geometry Scale (1.0 = standard size):  ",
      simulator.getScale,
      "This controls the size of the objects in the simulation", 0.01, 1000, false)
    scaleField.setEnabled(false)
    textInputsPanel.add(scaleField)
    paramPanel.add(togglesPanel, BorderLayout.CENTER)
    paramPanel.add(textInputsPanel, BorderLayout.SOUTH)
    paramPanel
  }

  /** override if you want to add more toggles. */
  protected def addAdditionalToggles(togglesPanel: JPanel): Unit = {}

  /** override if you want this panel of options for your simulation. */
  protected def createGlobalPhysicalParamPanel: JPanel = null

  /** For custom parameters that don't fall in other categories. */
  protected def createCustomParamPanel: JPanel

  protected def ok(): Unit = { // set the common rendering and global options
    simulator.setAntialiasing(antialiasingCheckbox.isSelected)
    simulator.setRecordAnimation(recordAnimationCheckbox.isSelected)
    simulator.setTimeStep(timeStepField.getValue)
    simulator.setNumStepsPerFrame(numStepsPerFrameField.getIntValue)
    simulator.setScale(scaleField.getValue)
    this.setVisible(false)
    simulator.repaint()
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    val source = e.getSource
    if (source eq startButton) ok()
    else if (source eq cancelButton) cancel()
  }
}