/* Copyright by Barry G. Becker, 2019-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT   */
package com.barrybecker4.simulation.complexmapping

import java.awt.event.{ActionEvent, ActionListener}
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}
import javax.swing._
import scala.compiletime.uninitialized
import ComplexMappingExplorer.{DEFAULT_INTERPOLATION_VAL, DEFAULT_MESH_DETAIL, DEFAULT_N}
import com.barrybecker4.simulation.common.ui.DynamicOptionsPanelSupport
import com.barrybecker4.simulation.complexmapping.algorithm.FunctionType
import DynamicOptions._

/**
  * Dynamic controls for the complex mapping explorer simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
object DynamicOptions {

  private val EXPONENT_SLIDER = "Exponent N"
  private val INTERPOLATION_SLIDER = "Interpolation from original"
  private val MESH_DETAIL_SLIDER = "Mesh granularity"
  private val SLIDER_DEBOUNCE_MS = 80
  private val SLIDER_PROPS = Array(
    new SliderProperties(EXPONENT_SLIDER, 1, 1000, DEFAULT_N, 1),
    new SliderProperties(INTERPOLATION_SLIDER, 0.0, 1.0, DEFAULT_INTERPOLATION_VAL, 200),
    new SliderProperties(MESH_DETAIL_SLIDER, 0.01, 0.4, DEFAULT_MESH_DETAIL, 100),
  )
}

class DynamicOptions private[complexmapping](var simulator: ComplexMappingExplorer)
  extends JPanel with ActionListener with SliderGroupChangeListener {

  DynamicOptionsPanelSupport.configureVerticalOptionsPanel(this)

  private var functionChoice: JComboBox[String] = uninitialized
  var sliderGroup: SliderGroup = uninitialized
  private var pendingSliderName: String = uninitialized
  private var pendingSliderValue: Double = 0.0
  private val debounceTimer: Timer = new Timer(SLIDER_DEBOUNCE_MS, (_: ActionEvent) => applyPendingSlider())
  debounceTimer.setRepeats(false)

  createUI()

  private def createUI(): Unit = {
    sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
    sliderGroup.setSliderListener(this)
    val legend: ContinuousColorLegend =
      new ContinuousColorLegend("Value color map", this.simulator.getColorMap, true)
    functionChoice = createFunctionDropdown

    add(new JLabel("Function to apply to a grid in the complex plane:"))
    add(functionChoice)
    add(sliderGroup)
    add(javax.swing.Box.createVerticalStrut(10))
    add(legend)
    add(DynamicOptionsPanelSupport.createScrollFiller(width = 10))
  }

  /** The dropdown menu at the top for selecting a mapping function. */
  private def createFunctionDropdown: JComboBox[String] = {
    val combo = new JComboBox[String]()
    combo.addActionListener(this)
    for (func <- FunctionType.values) {
      combo.addItem(func.name)
    }
    combo.setSelectedIndex(FunctionType.values.indexOf(ComplexMappingExplorer.DEFAULT_FUNCTION))
    combo
  }

  def reset(): Unit = sliderGroup.reset()

  /** Function combo selection applies immediately. */
  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == functionChoice) {
      simulator.setFunction(FunctionType.values(functionChoice.getSelectedIndex).function)
    }
  }

  /** Slider changes are debounced so drag events do not recompute the mesh on every tick. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    pendingSliderName = sliderName
    pendingSliderValue = value
    debounceTimer.restart()
  }

  private def applyPendingSlider(): Unit = {
    pendingSliderName match {
      case EXPONENT_SLIDER => simulator.setN(pendingSliderValue.toInt)
      case INTERPOLATION_SLIDER => simulator.setInterpolation(pendingSliderValue)
      case MESH_DETAIL_SLIDER => simulator.setMeshDetailIncrement(pendingSliderValue)
      case _ => ()
    }
  }
}
