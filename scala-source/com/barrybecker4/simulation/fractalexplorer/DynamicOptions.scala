// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer

import java.awt.{BorderLayout, GridLayout}
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._

import com.barrybecker4.math.complex.ComplexNumberRange
import com.barrybecker4.simulation.common.ui.DynamicOptionsPanelSupport
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}

import scala.compiletime.uninitialized


/**
  * Dynamic controls for the Fractal explorer simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  */
object DynamicOptions {
  private[fractalexplorer] val INITIAL_TIME_STEP = 10.0
  private[fractalexplorer] val DEFAULT_STEPS_PER_FRAME = 1
  private val ITER_SLIDER = "Max Iterations"
  private val TIMESTEP_SLIDER = "Num Rows per Frame"
  private val MIN_NUM_STEPS = (INITIAL_TIME_STEP / 10.0).toInt
  private val MAX_NUM_STEPS = (10.0 * INITIAL_TIME_STEP).toInt
  private val SLIDER_PROPS = Array(
    new SliderProperties(ITER_SLIDER, 100, 10000, FractalAlgorithm.DEFAULT_MAX_ITERATIONS, 1),
    new SliderProperties(TIMESTEP_SLIDER, MIN_NUM_STEPS, MAX_NUM_STEPS, INITIAL_TIME_STEP, 1)
  )
}

class DynamicOptions private[fractalexplorer](simulator: FractalExplorer)
  extends JPanel with ActionListener with SliderGroupChangeListener {

  private val sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
  private var useConcurrency: JCheckBox = uninitialized
  private var useFixedSize: JCheckBox = uninitialized
  private var useRunLengthOptimization: JCheckBox = uninitialized
  private var coordinate1: JLabel = uninitialized
  private var coordinate2: JLabel = uninitialized
  private val backButton = new JButton("Go Back")

  DynamicOptionsPanelSupport.configureVerticalOptionsPanel(this)

  sliderGroup.setSliderListener(this)
  val legend = new ContinuousColorLegend(null, simulator.getColorMap, true)
  add(sliderGroup)
  add(Box.createVerticalStrut(10))
  add(createCheckBoxes)
  add(Box.createVerticalStrut(10))
  add(legend)
  add(createCoordinatesView)
  backButton.addActionListener(this)
  add(backButton)
  add(DynamicOptionsPanelSupport.createScrollFiller(width = 10))

  def setCoordinates(range: ComplexNumberRange): Unit = {
    coordinate1.setText("c1: " + range.point1)
    coordinate2.setText("c2: " + range.point2)
  }

  private def createCheckBoxes: JPanel = {
    val algorithm = simulator.getAlgorithm
    useConcurrency = new JCheckBox("Parallel", algorithm.isParallelized)
    useConcurrency.setToolTipText("Take advantage of multiple processors for calculation and rendering if present.")
    useConcurrency.addActionListener(this)
    useFixedSize = new JCheckBox("Fixed Size", simulator.getUseFixedSize)
    useFixedSize.addActionListener(this)
    useRunLengthOptimization = new JCheckBox("Run Length Optimization", algorithm.getUseRunLengthOptimization)
    useRunLengthOptimization.addActionListener(this)
    val checkBoxes = new JPanel(new GridLayout(0, 1))
    checkBoxes.add(useConcurrency)
    checkBoxes.add(useFixedSize)
    checkBoxes.add(useRunLengthOptimization)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createCoordinatesView: JPanel = {
    val view = new JPanel
    view.setLayout(new BorderLayout)
    coordinate1 = new JLabel("Upper Left: ")
    coordinate2 = new JLabel("Lower Right: ")
    view.add(coordinate1, BorderLayout.NORTH)
    view.add(coordinate2, BorderLayout.CENTER)
    view
  }

  def reset(): Unit = sliderGroup.reset()

  override def actionPerformed(e: ActionEvent): Unit = {
    val algorithm = simulator.getAlgorithm
    if (e.getSource eq useConcurrency)
      algorithm.setParallelized(!algorithm.isParallelized)
    else if (e.getSource eq useFixedSize)
      simulator.setUseFixedSize(useFixedSize.isSelected)
    else if (e.getSource eq useRunLengthOptimization)
      algorithm.setUseRunLengthOptimization(useRunLengthOptimization.isSelected)
    else if (e.getSource eq backButton)
      algorithm.goBack()
  }

  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    val algorithm = simulator.getAlgorithm
    if (sliderName == DynamicOptions.ITER_SLIDER)
      algorithm.setMaxIterations(value.toInt)
    else if (sliderName == DynamicOptions.TIMESTEP_SLIDER)
      simulator.setTimeStep(value)
  }
}
