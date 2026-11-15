/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph

import com.barrybecker4.simulation.spirograph.model.GraphState
import com.barrybecker4.simulation.spirograph.model.ParametricEquations
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties


/** Slider indices into [[ControlSliderGroup.SLIDER_PROPS]] (declaration order). */
enum SliderId:
  case Radius1, Radius2, Position, Velocity, LineWidth, Segments

/**
  * Slider controls for spirograph radii, pen position, speed, and stroke.
  */
object ControlSliderGroup {

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
    state.params.r1 = SLIDER_PROPS(SliderId.Radius1.ordinal).getInitialValue.toFloat
    state.params.r2 = SLIDER_PROPS(SliderId.Radius2.ordinal).getInitialValue.toFloat
    state.params.pos = SLIDER_PROPS(SliderId.Position.ordinal).getInitialValue.toFloat
    state.setVelocity(SLIDER_PROPS(SliderId.Velocity.ordinal).getInitialValue.toInt)
    state.lineWidth = SLIDER_PROPS(SliderId.LineWidth.ordinal).getInitialValue.toInt
    state.numSegmentsPerRev = SLIDER_PROPS(SliderId.Segments.ordinal).getInitialValue.toInt
    state
  }
}

class ControlSliderGroup(var graphPanel: GraphPanel, var state: GraphState)
  extends SliderGroup(ControlSliderGroup.SLIDER_PROPS) with SliderGroupChangeListener {

  setSliderListener(this)

  def getRadius2Value: Double = getSliderValue(SliderId.Radius2.ordinal)

  /** Implements SliderChangeListener interface.
    * See SliderGroup
    * Maintains constraints between sliders.
    */
  override def sliderChanged(src: Int, sliderName: String, sliderValue: Double): Unit = {
    val value = sliderValue.toFloat
    SliderId.fromOrdinal(src) match {
      case SliderId.Radius1 =>
        var n: Float = getSliderValueAsInt(SliderId.Radius2.ordinal).toFloat
        if (n < 2 - value) {
          n = 1 - value
          setSliderValue(SliderId.Radius2.ordinal, n)
          state.setR2(n)
        }
        setSliderMinimum(SliderId.Radius2.ordinal, 2 - value)
        state.setR1(value)
      case SliderId.Radius2 => state.setR2(value)
      case SliderId.Position => state.setPos(value)
      case SliderId.Velocity => state.setVelocity(value.toInt)
      case SliderId.LineWidth => state.lineWidth = value.toInt
      case SliderId.Segments => state.numSegmentsPerRev = value.toInt
    }
    autoUpdate()
  }

  private def autoUpdate(): Unit = {
    if (state.isMaxVelocity) {
      graphPanel.reset()
      graphPanel.drawCompleteGraph()
    }
    else graphPanel.repaint()
  }

  /** @return the x and y parametric equations for the equation labels. */
  def getEquations: ParametricEquations = {
    val rad = getSliderValueAsInt(SliderId.Radius2.ordinal)
    val combinedRad = getSliderValueAsInt(SliderId.Radius1.ordinal) + rad
    val pos = getSliderValueAsInt(SliderId.Position.ordinal)
    ParametricEquations(rad, combinedRad, pos)
  }
}
