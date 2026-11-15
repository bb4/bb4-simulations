/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph.model

import java.awt.geom.Point2D


/**
  * Hold parameters that define the current spirograph state.
  */
class Parameters() {

  /** radius of the main circle */
  var r1 = .0f
  /** radius of the secondary outer circle */
  var r2 = .0f
  /** offset position from main circle */
  var pos = .0f
  /** angle of the main circle for current state. */
  var theta = .0f
  /** angle of the outer secondary circle for current state. */
  var phi = .0f
  var x = .0f
  var y = .0f

  def initialize(width: Int, height: Int): Unit = {
    resetAngle()
    val (cx, cy) = SpirographMath.center(width, height, r1, r2, 0f)
    val (px, py) = SpirographMath.penPoint(cx, cy, pos, 0f)
    x = px
    y = py
  }

  def sign: Int = SpirographMath.sign(r2)

  /** Reset to initial values. */
  def resetAngle(): Unit = {
    theta = 0.0f
    phi = 0.0f
  }

  def getCenter(width: Int, height: Int): Point2D = {
    val (cx, cy) = SpirographMath.center(width, height, r1, r2, theta)
    new Point2D.Double(cx, cy)
  }

  /** Set our values from another parameters instance. */
  def copyFrom(other: Parameters): Unit = {
    r1 = other.r1
    r2 = other.r2
    pos = other.pos
    theta = other.theta
    phi = other.phi
    x = other.x
    y = other.y
  }
}
