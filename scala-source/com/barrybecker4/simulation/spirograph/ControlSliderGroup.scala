/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph

import com.barrybecker4.simulation.spirograph.model.GraphState
import com.barrybecker4.simulation.spirograph.model.ParametricEquations
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties


/**
  * That old spirograph game from the 70's brought into the computer age
  * Based on work originally done by David Little.
  *
  * Use checkboxes instead of buttons for
  *  - show decoration (was show axis)
  *
  * @author Barry Becker
  */
object ControlSliderGroup { // slider indices
  private val RADIUS1 = 0
  private val RADIUS2 = 1
  private val POSITION = 2
  private val VELOCITY = 3
  private val LINE_WIDTH = 4
  private val SEGMENTS = 5
  /** Initialize the sliders in the group */
  private val SLIDER_PROPS = Array(
    new SliderProperties("Radius1", 5, 255, 60),
    new SliderProperties("Radius2", -59, 200, 60),
    new SliderProperties("Position", -300, 300, 60),
    new SliderProperties("Speed", 1, GraphState.VELOCITY_MAX, GraphState.VELOCITY_MAX / 2),
    new SliderProperties("Line Width", 1, 50, GraphState.INITIAL_LINE_WIDTH),
    new SliderProperties("Num Segments/Revolution",
      GraphState.DEFAULT_NUM_SEGMENTS / 12, 4 * GraphState.DEFAULT_NUM_SEGMENTS, GraphState.DEFAULT_NUM_SEGMENTS))

  def createGraphState: GraphState = {
    val state = new GraphState
    state.params.r1 = SLIDER_PROPS(RADIUS1).getInitialValue.toFloat
    state.params.r2 = SLIDER_PROPS(RADIUS2).getInitialValue.toFloat
    state.params.pos = SLIDER_PROPS(POSITION).getInitialValue.toFloat
    state.setVelocity(SLIDER_PROPS(VELOCITY).getInitialValue.toInt)
    state.width = SLIDER_PROPS(LINE_WIDTH).getInitialValue.toInt
    state.numSegmentsPerRev = SLIDER_PROPS(SEGMENTS).getInitialValue.toInt
    state
  }
}

class ControlSliderGroup(var graphPanel: GraphPanel, var state: GraphState)
  extends SliderGroup(ControlSliderGroup.SLIDER_PROPS) with SliderGroupChangeListener {

  setSliderListener(this)

  def getRadius2Value: Double = getSliderValue(ControlSliderGroup.RADIUS2)

  /** Implements SliderChangeListener interface.
    * See SliderGroup
    * Maintains constraints between sliders.
    */
  override def sliderChanged(src: Int, sliderName: String, sliderValue: Double): Unit = {
    // I know that all the sliders are integer based.
    val value = sliderValue.toFloat
    if (src == ControlSliderGroup.RADIUS1) {
      var n: Float = getSliderValueAsInt(ControlSliderGroup.RADIUS2).toFloat
      if (n < 2 - value) {
        n = 1 - value
        setSliderValue(ControlSliderGroup.RADIUS2, n)
        state.setR2(n.toFloat)
      }
      setSliderMinimum(ControlSliderGroup.RADIUS2, 2 - value)
      state.setR1(value.toFloat)
    }
    else if (src == ControlSliderGroup.RADIUS2) state.setR2(value)
    else if (src == ControlSliderGroup.POSITION) state.setPos(value)
    else if (src == ControlSliderGroup.VELOCITY) state.setVelocity(value.toInt)
    else if (src == ControlSliderGroup.LINE_WIDTH) state.width = value.toInt
    else if (src == ControlSliderGroup.SEGMENTS) state.numSegmentsPerRev = value.toInt
    else throw new IllegalArgumentException("Unexpected slider index=" + src)
    autoUpdate()
  }

  private def autoUpdate(): Unit = {
    if (state.isMaxVelocity) {
      graphPanel.reset()
      graphPanel.drawCompleteGraph()
    }
    else graphPanel.repaint()
  }

  /** @return the x and y parametric equations in a 2 element list.*/
  def getEquations: ParametricEquations = {
    val rad = getSliderValueAsInt(ControlSliderGroup.RADIUS2)
    val combinedRad = getSliderValueAsInt(ControlSliderGroup.RADIUS1) + rad
    val pos = getSliderValueAsInt(ControlSliderGroup.POSITION)
    ParametricEquations(rad, combinedRad, pos)
  }
}