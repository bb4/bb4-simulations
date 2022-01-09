/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT   */
package com.barrybecker4.simulation.complexmapping

import java.awt.event.{ActionEvent, ActionListener, MouseAdapter, MouseEvent}
import java.awt.{BorderLayout, Dimension, GridLayout}
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}
import javax.swing._
import ComplexMappingExplorer.{DEFAULT_INTERPOLATION_VAL, DEFAULT_MESH_DETAIL, DEFAULT_ORIG_GRID_BOUNDS }
import com.barrybecker4.simulation.complexmapping.algorithm.FunctionType

/**
  * Dynamic controls for the complex mapping explorer simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
object DynamicOptions {

  private[complexmapping] val DEFAULT_N = 2
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

  private var functionChoice: JComboBox[String] = _
  var sliderGroup: SliderGroup = _

  createUI()

  private def createUI(): Unit = {
    sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
    sliderGroup.setSliderListener(this)
    val legend: ContinuousColorLegend =
      new ContinuousColorLegend("Value color map", this.simulator.getColorMap, true)
    val checkBoxes: JPanel = createCheckBoxes
    val gridBox: JPanel = createOriginalGridBoxUI
    functionChoice = createFunctionDropdown

    add(new JLabel("Function to apply to a grid in the complex plane:"))
    add(functionChoice)
    add(sliderGroup)
    add(javax.swing.Box.createVerticalStrut(10))
    add(checkBoxes)
    add(javax.swing.Box.createVerticalStrut(10))
    add(legend)
    add(gridBox)
    val updateButton = new JButton("Update")
    updateButton.addActionListener(this)
    add(updateButton)
    val fill = new JPanel
    fill.setPreferredSize(new Dimension(10, 1000))
    add(fill)
  }


  /** The dropdown menu at the top for selecting an algorithm for solving the puzzle.
    * @return a dropdown/down component.
    */
  private def createFunctionDropdown: JComboBox[String] = {
    functionChoice = new JComboBox[String]()
    functionChoice.addActionListener(this)
    for (func <- FunctionType.values) {
      functionChoice.addItem(func.name)
    }
    functionChoice.setSelectedIndex(FunctionType.values.indexOf(ComplexMappingExplorer.DEFAULT_FUNCTION))
    functionChoice
  }

  private def createCheckBoxes = {
    val checkBoxes = new JPanel(new GridLayout(0, 1))
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createOriginalGridBoxUI = {
    val view = new JPanel
    view.setLayout(new BorderLayout)

    val title = new JLabel("Original grid extent: ")
    view.add(title, BorderLayout.NORTH)

    val boundingBoxPanel = new JPanel()
    boundingBoxPanel.setLayout(new BoxLayout(boundingBoxPanel, BoxLayout.Y_AXIS))
    view.add(boundingBoxPanel, BorderLayout.CENTER)
    view
  }

  def reset(): Unit = sliderGroup.reset()

  /** One of the buttons was pressed. */
  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == functionChoice) {
      simulator.setFunction(FunctionType.fromOrdinal(functionChoice.getSelectedIndex).function)
    } else simulator.redraw()
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    sliderName match {
      case DynamicOptions.EXPONENT_SLIDER => simulator.setN(value.toInt)
      case DynamicOptions.INTERPOLATION_SLIDER => simulator.setInterpolation(value)
      case DynamicOptions.MESH_DETAIL_SLIDER => simulator.setMeshDetailIncrement(value)
    }
  }
}
