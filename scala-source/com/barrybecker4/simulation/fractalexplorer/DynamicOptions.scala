// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer

import java.awt.{BorderLayout, Dimension, GridLayout}
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._

import com.barrybecker4.math.complex.ComplexNumberRange
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}


/**
  * Dynamic controls for the Fractal explorer simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  *
  * @author Barry Becker
  */
object DynamicOptions {
  private[fractalexplorer] val INITIAL_TIME_STEP = 10.0
  private[fractalexplorer] val DEFAULT_STEPS_PER_FRAME = 1
  private val ITER_SLIDER = "Max Iterations"
  private val TIMESTEP_SLIDER = "Num Rows per Frame"
  private val MIN_NUM_STEPS = (INITIAL_TIME_STEP / 10.0).toInt
  private val MAX_NUM_STEPS = (10.0 * INITIAL_TIME_STEP).toInt
  private val SLIDER_PROPS = Array(new SliderProperties(ITER_SLIDER, 100, 10000, FractalAlgorithm.DEFAULT_MAX_ITERATIONS, 1), new SliderProperties(TIMESTEP_SLIDER, MIN_NUM_STEPS, MAX_NUM_STEPS, INITIAL_TIME_STEP, 1))
}

class DynamicOptions private[fractalexplorer](var simulator: FractalExplorer)
  extends JPanel with ActionListener with SliderGroupChangeListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))
  private var sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
  sliderGroup.setSliderListener(this)
  val legend: ContinuousColorLegend = new ContinuousColorLegend(null, this.simulator.getColorMap, true)
  val checkBoxes: JPanel = createCheckBoxes
  val coordinates: JPanel = createCoordinatesView
  add(sliderGroup)
  add(Box.createVerticalStrut(10))
  add(checkBoxes)
  add(Box.createVerticalStrut(10))
  add(legend)
  add(coordinates)
  private var backButton = new JButton("Go Back")
  backButton.addActionListener(this)
  add(backButton)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(10, 1000))
  add(fill)
  private var useConcurrency: JCheckBox = _
  private var useFixedSize: JCheckBox = _
  private var useRunLengthOptimization: JCheckBox = _
  private var coordinate1: JLabel = _
  private var coordinate2: JLabel = _

  def setCoordinates(range: ComplexNumberRange): Unit = {
    coordinate1.setText("c1: " + range.point1)
    coordinate2.setText("c2: " + range.point2)
  }

  private def createCheckBoxes = {
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

  private def createCoordinatesView = {
    val view = new JPanel
    view.setLayout(new BorderLayout)
    coordinate1 = new JLabel("Upper Left: ")
    coordinate2 = new JLabel("Lower Right: ")
    view.add(coordinate1, BorderLayout.NORTH)
    view.add(coordinate2, BorderLayout.CENTER)
    view
  }

  def reset(): Unit = {
    sliderGroup.reset()
  }

  /**
    * One of the buttons was pressed.
    */
  override def actionPerformed(e: ActionEvent): Unit = { //RDRenderingOptions renderingOptions = simulator.getRenderingOptions();
    val algorithm = simulator.getAlgorithm
    if (e.getSource eq useConcurrency) {
      val isParallelized = !algorithm.isParallelized
      algorithm.setParallelized(isParallelized)
    }
    else if (e.getSource eq useFixedSize) simulator.setUseFixedSize(useFixedSize.isSelected)
    else if (e.getSource eq useRunLengthOptimization) algorithm.setUseRunLengthOptimization(useRunLengthOptimization.isSelected)
    else if (e.getSource eq backButton) algorithm.goBack()
  }

  /**
    * One of the sliders was moved.
    */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    val algorithm = simulator.getAlgorithm
    if (sliderName == DynamicOptions.ITER_SLIDER) algorithm.setMaxIterations(value.toInt)
    else if (sliderName == DynamicOptions.TIMESTEP_SLIDER) simulator.setTimeStep(value)
  }
}
