/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph.model

import java.awt.geom.Point2D


/**
  * Hold parameters that define the current spirograph state.
  * TODO: remove getters and setters
  * @author Barry Becker
  */
class Parameters() {
  private var r1 = .0f
  private var r2 = .0f
  private var pos = .0f
  private var theta = .0f
  private var phi = .0f
  private var x = .0f
  private var y = .0f
  private var sign = 1

  def initialize(width: Int, height: Int): Unit = {
    resetAngle()
    setX((width >> 1) + r1 + (r2 + sign) + pos)
    setY(height >> 1)
  }

  /** @return radius of the main circle */
  def getR1: Float = r1

  def setR1(r1: Float): Unit = {
    this.r1 = r1
  }

  /** @return radius of the secondary outer circle */
  def getR2: Float = r2

  def setR2(r2: Float): Unit = {
    this.r2 = r2
    setSign(if (r2 < 0) -1
    else 1)
  }

  /** @return offset position from main circle */
  def getPos: Float = pos

  def setPos(p: Float): Unit = {
    pos = p
  }

  /** @return angle of the main circle for current state. */
  def getTheta: Float = theta

  def setTheta(theta: Float): Unit = {
    this.theta = theta
  }

  /** @return angle of the outer secondary circle for current state. */
  def getPhi: Float = phi

  def setPhi(phi: Float): Unit = {
    this.phi = phi
  }

  def getSign: Int = sign

  private def setSign(sign: Int): Unit = {
    this.sign = sign
  }

  def getX: Float = x

  def setX(x: Float): Unit = {
    this.x = x
  }

  def getY: Float = y

  def setY(y: Float): Unit = {
    this.y = y
  }

  /**
    * reset to initial values.
    */
  def resetAngle(): Unit = {
    setTheta(0.0f)
    setPhi(0.0f)
  }

  def getCenter(width: Int, height: Int): Point2D = {
    val r1 = getR1
    val r2 = getR2
    val theta = getTheta
    new Point2D.Double((width >> 1) + (r1 + r2) * Math.cos(theta), (height >> 1) - (r1 + r2) * Math.sin(theta))
  }

  /**
    * set our values from another parameters instance.
    */
  def copyFrom(other: Parameters): Unit = {
    r1 = other.getR1
    r2 = other.getR2
    pos = other.getPos
    theta = other.getTheta
    phi = other.getPhi
    sign = other.getSign
    x = other.getX
    y = other.getY
  }
}