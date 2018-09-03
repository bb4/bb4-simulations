/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph.model

import com.barrybecker4.common.math.MathUtil
import com.barrybecker4.ui.sliders.ColorChangeListener
import java.awt.Color


object GraphState {
  val INITIAL_LINE_WIDTH = 10
  val VELOCITY_MAX = 120
  val DEFAULT_NUM_SEGMENTS = 200
}

/**
  * Holds shared state information for the spirograph. The view model.
  * @author Barry Becker
  */
class GraphState() extends ColorChangeListener {

  var width: Int = GraphState.INITIAL_LINE_WIDTH
  var color: Color = _
  var numSegmentsPerRev = 0
  var showDecoration = true
  var params: Parameters = new Parameters
  var oldParams: Parameters = new Parameters

  private var rendering: Boolean = false
  private var velocity = 2
  private var listeners = List[GraphStateChangeListener]()

  def initialize(width: Int, height: Int): Unit = {
    params.initialize(width, height)
    recordValues()
  }

  def addStateListener(listener: GraphStateChangeListener): Unit =
    listeners +:= listener

  override def colorChanged(c: Color): Unit =
    color = c

  def setR1(r1: Float): Unit = {
    params.r1 = r1
    notifyParameterChanged()
  }

  def setR2(r2: Float): Unit = {
    params.r2 = r2
    notifyParameterChanged()
  }

  def setPos(pos: Float): Unit = {
    params.pos = pos
    notifyParameterChanged()
  }

  def setVelocity(velocity: Int): Unit = {
    this.velocity = velocity
    if (isMaxVelocity) notifyParameterChanged()
  }

  def isMaxVelocity: Boolean = velocity == GraphState.VELOCITY_MAX

  /** @return number of milliseconds to delay between animation steps to slow rendering based on velocity value. */
  def getDelayMillis: Int = {
      if (velocity < GraphState.VELOCITY_MAX / 2)
        5 * (GraphState.VELOCITY_MAX + (GraphState.VELOCITY_MAX / 2 - velocity)) / velocity
      else (5 * GraphState.VELOCITY_MAX) / velocity - 5
  }

  def isRendering: Boolean = rendering

  def setRendering(rendering: Boolean): Unit = {
    this.rendering = rendering
    if (!rendering) notifyRenderingComplete()
  }

  /** @return the number of complete revolutions needed until the curve will overwrite itself. */
  def getNumRevolutions: Int = {
    val sign = params.sign
    val gcd = MathUtil.gcd(params.r1.toLong, (sign * params.r2).toLong)
    ((sign * params.r2) / gcd).toInt
  }

  /** reset to initial values. */
  def reset(): Unit = params.resetAngle()

  /** Set the old values from the current. */
  def recordValues(): Unit = oldParams.copyFrom(params)

  private def notifyParameterChanged(): Unit =
    for (listener <- listeners) listener.parameterChanged()

  private def notifyRenderingComplete(): Unit =
    for (listener <- listeners) listener.renderingComplete()
}