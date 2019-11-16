/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT   */
package com.barrybecker4.simulation.complexmapping

import java.awt.event.{ActionEvent, ActionListener, MouseAdapter, MouseEvent}
import java.awt.{BorderLayout, Dimension, GridLayout}

import com.barrybecker4.common.math.ComplexNumberRange
import com.barrybecker4.simulation.complexmapping.algorithm.functions.RiemannZetaFunction
import com.barrybecker4.ui.components.NumberInput
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}
import javax.swing._
import ComplexMappingExplorer.{DEFAULT_INTERPOLATION_VAL, DEFAULT_MESH_DETAIL, DEFAULT_VIEWPORT}
import com.barrybecker4.simulation.complexmapping.algorithm.model.Box
import javax.vecmath.Point2d

/**
  * Dynamic controls for the complex mapping explorer simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
object DynamicOptions {

  private[complexmapping] val DEFAULT_N = 1
  private val EXPONENT_SLIDER = "Exponent N"
  private val INTERPOLATION_SLIDER = "Interpolation from original"
  private val MESH_DETAIL_SLIDER = "Mesh granularity"
  private val SLIDER_PROPS = Array(
    new SliderProperties(EXPONENT_SLIDER, 1, 1000, DEFAULT_N, 1),
    new SliderProperties(INTERPOLATION_SLIDER, 0.0, 1.0, DEFAULT_INTERPOLATION_VAL, 200),
    new SliderProperties(MESH_DETAIL_SLIDER, 0.01, 0.4, DEFAULT_MESH_DETAIL, 100),
  )
}

class DynamicOptions private[complexmapping](var simulator: ComplexMappingExplorer)
  extends JPanel with ActionListener with SliderGroupChangeListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))
  private var sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
  sliderGroup.addSliderChangeListener(this)
  val legend: ContinuousColorLegend = new ContinuousColorLegend("Value color map", this.simulator.getColorMap, true)
  val checkBoxes: JPanel = createCheckBoxes
  val viewport: JPanel = createViewpointUI
  add(sliderGroup)
  add(javax.swing.Box.createVerticalStrut(10))
  add(checkBoxes)
  add(javax.swing.Box.createVerticalStrut(10))
  add(legend)
  add(viewport)
  private var updateButton = new JButton("Update")
  updateButton.addActionListener(this)
  add(updateButton)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(10, 1000))
  add(fill)

  private var coordinate1: JLabel = _
  private var coordinate2: JLabel = _
  private var upperLeftX: NumberInput = _
  private var upperLeftY: NumberInput = _
  private var lowerRightX: NumberInput = _
  private var lowerRightY: NumberInput = _

  def setCoordinates(range: ComplexNumberRange): Unit = {
    coordinate1.setText("c1: " + range.point1)
    coordinate2.setText("c2: " + range.point2)
  }

  private def createCheckBoxes = {
    val checkBoxes = new JPanel(new GridLayout(0, 1))
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createViewpointUI = {
    val view = new JPanel
    view.setLayout(new BorderLayout)
    coordinate2 = new JLabel("Lower Right: ")
    //view.add(createUpperLeftInput, BorderLayout.NORTH)
    //view.add(createLowerRightInput, BorderLayout.CENTER)
    view
  }

  private def createUpperLeftInput: JPanel = {
    val panel = new JPanel()
    panel.setLayout(new BorderLayout)
    coordinate1 = new JLabel("Upper Left: ")
    upperLeftX = new NumberInput("x: ",
      DEFAULT_VIEWPORT.upperLeft.x, "x coordinate of upper left viewport.", -10, 100.0, false)
    upperLeftY = new NumberInput("y: ",
      DEFAULT_VIEWPORT.upperLeft.y, "y coordinate of upper left viewport.", 0, 10.0, false)

    panel.add(coordinate1, BorderLayout.NORTH)
    panel.add(upperLeftX, BorderLayout.WEST)
    panel.add(upperLeftY, BorderLayout.CENTER)
    panel
  }

  private def createLowerRightInput: JPanel = {
    val panel = new JPanel()
    panel.setLayout(new BorderLayout)
    coordinate2 = new JLabel("Lower right: ")
    lowerRightX = new NumberInput("x: ",
      DEFAULT_VIEWPORT.lowerRight.x, "x coordinate of lower right viewport.", 0, 100.0, false)
    lowerRightY = new NumberInput("y: ",
      DEFAULT_VIEWPORT.lowerRight.y, "y coordinate of lower right viewport.", -10, 0.0, false)
    panel.add(coordinate2, BorderLayout.NORTH)
    panel.add(lowerRightX, BorderLayout.WEST)
    panel.add(lowerRightY, BorderLayout.CENTER)
    panel
  }

  def reset(): Unit = sliderGroup.reset()

  /** One of the buttons was pressed. */
  override def actionPerformed(e: ActionEvent): Unit = {
      simulator.redraw();
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    sliderName match {
      case DynamicOptions.EXPONENT_SLIDER => simulator.setFunction(RiemannZetaFunction(value.toInt))
      case DynamicOptions.INTERPOLATION_SLIDER => simulator.setInterpolation(value)
      case DynamicOptions.MESH_DETAIL_SLIDER => simulator.setMeshDetailIncrement(value)
    }
  }
}
