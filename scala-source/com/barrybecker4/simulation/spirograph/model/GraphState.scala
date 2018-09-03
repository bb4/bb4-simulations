/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph.model

import com.barrybecker4.common.math.MathUtil
import com.barrybecker4.ui.sliders.ColorChangeListener
import java.awt._
import java.util


/**
  * Holds shared state information for the spirograph.
  * The view model. TODO: remove getters/setters
  * @author Barry Becker
  */
object GraphState {
  val INITIAL_LINE_WIDTH = 10
  val VELOCITY_MAX = 120
  val DEFAULT_NUM_SEGMENTS = 200
}

class GraphState() extends ColorChangeListener {

  var params: Parameters = new Parameters
  var oldParams: Parameters = new Parameters
  private var rendering: Boolean = false
  private var width = GraphState.INITIAL_LINE_WIDTH
  private var color: Color = _
  private var numSegmentsPerRev = 0
  private var showingDecoration = true
  private var velocity = 2
  private var listeners = new util.LinkedList[GraphStateChangeListener]

  def initialize(width: Int, height: Int): Unit = {
    params.initialize(width, height)
    recordValues()
  }

  def addStateListener(listener: GraphStateChangeListener): Unit = {
    listeners.add(listener)
  }

  override def colorChanged(color: Color): Unit = {
    setColor(color)
  }

  def getColor: Color = color

  def setColor(color: Color): Unit = {
    this.color = color
  }

  def setR1(r1: Float): Unit = {
    params.setR1(r1)
    notifyParameterChanged()
  }

  def setR2(r2: Float): Unit = {
    params.setR2(r2)
    notifyParameterChanged()
  }

  def setPos(pos: Float): Unit = {
    params.setPos(pos)
    notifyParameterChanged()
  }

  def setVelocity(velocity: Int): Unit = {
    this.velocity = velocity
    if (isMaxVelocity) notifyParameterChanged()
  }

  def isMaxVelocity: Boolean = velocity == GraphState.VELOCITY_MAX

  /**
    * @return number of milliseconds to delay between animation steps to slow rendering based on velocity value.
    */
  def getDelayMillis: Int = {
    var delay = 0
    if (velocity < GraphState.VELOCITY_MAX / 2) delay = 5 * (GraphState.VELOCITY_MAX + (GraphState.VELOCITY_MAX / 2 - velocity)) / velocity
    else delay = (5 * GraphState.VELOCITY_MAX) / velocity - 5
    delay
  }

  def getWidth: Int = width

  def setWidth(width: Int): Unit = {
    this.width = width
  }

  def setNumSegmentsPerRev(numSegments: Int): Unit = {
    numSegmentsPerRev = numSegments
  }

  def getNumSegmentsPerRev: Int = numSegmentsPerRev

  def isRendering: Boolean = rendering

  def setRendering(rendering: Boolean): Unit = {
    this.rendering = rendering
    if (!rendering) notifyRenderingComplete()
  }

  def showDecoration: Boolean = showingDecoration

  def setShowDecoration(show: Boolean): Unit = {
    showingDecoration = show
  }

  /** @return the number of complete revolutions needed until the curve will overwrite itself. */
  def getNumRevolutions: Int = {
    val sign = params.getSign
    val gcd = MathUtil.gcd(params.getR1.toLong, (sign * params.getR2).toLong)
    ((sign * params.getR2) / gcd).toInt
  }

  /** reset to initial values. */
  def reset(): Unit = {
    params.resetAngle()
  }

  /**
    * set the old values from the current.
    */
  def recordValues(): Unit = {
    oldParams.copyFrom(params)
  }

  private def notifyParameterChanged(): Unit = {
    import scala.collection.JavaConversions._
    for (listener <- listeners) {
      listener.parameterChanged()
    }
  }

  private def notifyRenderingComplete(): Unit = {
    import scala.collection.JavaConversions._
    for (listener <- listeners) {
      listener.renderingComplete()
    }
  }
}