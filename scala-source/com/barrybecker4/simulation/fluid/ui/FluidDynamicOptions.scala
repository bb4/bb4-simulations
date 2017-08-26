// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import com.barrybecker4.simulation.fluid.model.FluidEnvironment
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties
import javax.swing._
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener


/**
  * Dynamic controls for the Fluid simulation.
  *
  * @author Barry Becker
  */
object FluidDynamicOptions {
  private val DR_SLIDER = "Diffusion Rate"
  private val VISC_SLIDER = "Viscosity"
  private val FORCE_SLIDER = "Force"
  private val SD_SLIDER = "Source Density"
  private val NUM_ITERATIONS_SLIDER = "Num Solver Iterations"
  private val NS_SLIDER = "Num Steps per Frame"
  private val TIME_STEP_SLIDER = "Time Step"
  private val MIN_STEPS = Math.ceil(FluidSimulator.DEFAULT_STEPS_PER_FRAME / 10.0)
  private val MAX_STEPS = 20.0 * FluidSimulator.DEFAULT_STEPS_PER_FRAME
  private val SLIDER_PROPS = Array(
    new SliderProperties(DR_SLIDER, 0, 10.0, FluidEnvironment.DEFAULT_DIFFUSION_RATE, 100.0),
    new SliderProperties(VISC_SLIDER, 0, 50.0, FluidEnvironment.DEFAULT_VISCOSITY, 100.0),
    new SliderProperties(FORCE_SLIDER, 0.01, 30.0, InteractionHandler.DEFAULT_FORCE, 100.0),
    new SliderProperties(SD_SLIDER, 0.01, 4.0, InteractionHandler.DEFAULT_SOURCE_DENSITY, 100.0),
    new SliderProperties(NUM_ITERATIONS_SLIDER, 1, 100, FluidEnvironment.DEFAULT_NUM_SOLVER_ITERATIONS, 1.0),
    new SliderProperties(NS_SLIDER, MIN_STEPS, MAX_STEPS, FluidSimulator.DEFAULT_STEPS_PER_FRAME, 1.0),
    new SliderProperties(TIME_STEP_SLIDER, 0.001, 0.1, FluidSimulator.INITIAL_TIME_STEP, 1000.0)
  )
}

class FluidDynamicOptions private[ui](var simulator: FluidSimulator) extends JPanel with ActionListener with SliderGroupChangeListener {

  private var useConcurrentRendering: JCheckBox  = _
  private var useLinearInterpolation: JCheckBox = _
  private var showVelocities: JCheckBox = _
  private var showGrid: JCheckBox = _

  setBorder(BorderFactory.createEtchedBorder)
  val controlsPanel = new JPanel
  controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS))
  this.add(controlsPanel, BorderLayout.CENTER)
  private var sliderGroup = new SliderGroup(FluidDynamicOptions.SLIDER_PROPS)
  sliderGroup.addSliderChangeListener(this)
  sliderGroup.setPreferredSize(new Dimension(300, 260))
  val checkBoxes: JPanel = createCheckBoxes
  val legend = new ContinuousColorLegend(null, this.simulator.getRenderer.getColorMap, true)

  controlsPanel.add(sliderGroup)
  controlsPanel.add(Box.createVerticalStrut(10))
  controlsPanel.add(checkBoxes)
  controlsPanel.add(Box.createVerticalStrut(10))
  controlsPanel.add(legend)

  private def createCheckBoxes = {
    val renderOpts = simulator.getRenderer.getOptions
    useConcurrentRendering = createCheckBox(
      "Parallel rendering", "Will take advantage of multiple processors for rendering if present.",
      renderOpts.isParallelized)
    useLinearInterpolation = createCheckBox(
      "Use linear interpolation", "If checked, use linear interpolation when rendering, to give a smoother look.",
      renderOpts.getUseLinearInterpolation)
    showVelocities = createCheckBox("Show velocities", "if checked, show velocity vectors", renderOpts.getShowVelocities)
    showGrid = createCheckBox("Show grid", "Draw the background grid that shows the cells.", renderOpts.getShowGrid)
    val checkBoxes = new JPanel(new GridLayout(0, 2))
    checkBoxes.add(useConcurrentRendering)
    checkBoxes.add(useLinearInterpolation)
    checkBoxes.add(showVelocities)
    checkBoxes.add(showGrid)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createCheckBox(label: String, tooltip: String, initiallyChecked: Boolean) = {
    val cb = new JCheckBox(label, initiallyChecked)
    cb.setToolTipText(tooltip)
    cb.addActionListener(this)
    cb
  }

  def reset(): Unit = {
    sliderGroup.reset()
    // make sure we honor current check selections
    val renderOpts = simulator.getRenderer.getOptions
    renderOpts.setShowGrid(showGrid.isSelected)
    renderOpts.setShowVelocities(showVelocities.isSelected)
    renderOpts.setUseLinearInterpolation(useLinearInterpolation.isSelected)
  }

  /** One of the buttons was pressed */
  override def actionPerformed(e: ActionEvent): Unit = {
    val renderer = simulator.getRenderer
    val renderOpts = renderer.getOptions
    if (e.getSource eq useConcurrentRendering) renderOpts.isParallelized = !renderOpts.isParallelized
    else if (e.getSource eq useLinearInterpolation) renderOpts.setUseLinearInterpolation(!renderOpts.getUseLinearInterpolation)
    else if (e.getSource eq showVelocities) renderOpts.setShowVelocities(!renderOpts.getShowVelocities)
    else if (e.getSource eq showGrid) renderOpts.setShowGrid(!renderOpts.getShowGrid)
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    sliderName match {
      case FluidDynamicOptions.VISC_SLIDER => simulator.getEnvironment.setViscosity(value)
      case FluidDynamicOptions.DR_SLIDER => simulator.getEnvironment.setDiffusionRate(value)
      case FluidDynamicOptions.FORCE_SLIDER => simulator.getInteractionHandler.setForce(value)
      case FluidDynamicOptions.SD_SLIDER => simulator.getInteractionHandler.setSourceDensity(value)
      case FluidDynamicOptions.NUM_ITERATIONS_SLIDER => simulator.getEnvironment.setNumSolverIterations(value.toInt)
      case FluidDynamicOptions.NS_SLIDER => simulator.setNumStepsPerFrame(value.toInt)
      case FluidDynamicOptions.TIME_STEP_SLIDER => simulator.setTimeStep(value)
    }
  }
}
