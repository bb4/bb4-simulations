/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT   */
package com.barrybecker4.simulation.complexmapping

import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{BorderLayout, Dimension, GridLayout}

import com.barrybecker4.common.math.ComplexNumberRange
import com.barrybecker4.simulation.complexmapping.algorithm.functions.RiemannZetaFunction
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}
import javax.swing._

/**
  * Dynamic controls for the Fractal explorer simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
object DynamicOptions {

  private[complexmapping] val DEFAULT_N = 1
  private val EXPONENT_SLIDER = "Exponent N"
  private val SLIDER_PROPS = Array(new SliderProperties(EXPONENT_SLIDER, 1, 100, DEFAULT_N, 1))
}

class DynamicOptions private[complexmapping](var simulator: ComplexMappingExplorer)
  extends JPanel with ActionListener with SliderGroupChangeListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))
  private var sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
  sliderGroup.addSliderChangeListener(this)
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

  private var coordinate1: JLabel = _
  private var coordinate2: JLabel = _

  def setCoordinates(range: ComplexNumberRange): Unit = {
    coordinate1.setText("c1: " + range.point1)
    coordinate2.setText("c2: " + range.point2)
  }

  private def createCheckBoxes = {
    val checkBoxes = new JPanel(new GridLayout(0, 1))
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

  def reset(): Unit = sliderGroup.reset()

  /** One of the buttons was pressed. */
  override def actionPerformed(e: ActionEvent): Unit = {
    val function = simulator.getFunction
  }

  /**
    * One of the sliders was moved.
    */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    if (sliderName == DynamicOptions.EXPONENT_SLIDER) {
      simulator.setFunction(RiemannZetaFunction(value.toInt))
    }
  }
}
