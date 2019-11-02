// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

/**
  * Keeps track of the current and last velocities for the cell.
  * The u component heads out of the cell to the left, and
  * the v component heads out of the cell to the top.
  * velocities in x, y directions are defined at the center of each face.
  *
  * @author Barry Becker
  */
class CellVelocity {

  /** uip = u(i+0.5, j, k) */
  private val uip = new Array[Double](2)

  /** vjp = v(i, j+0.5, k) */
  private val vjp = new Array[Double](2)

  /** use this to switch between current and last copies of fields. (hack) */
  private var current = 0
  //uip(0) = uip(1)
  //vjp(0) = vjp(1)

  /** global swap of fields (use with care). (hack) */
  def step() {current = 1 - current}

  private[model] def passThrough(): Unit = {
    uip(1 - current) = uip(current) // + dt * forceX;
    vjp(1 - current) = vjp(current) // + dt * forceY;
  }

  private[model] def initializeU(u: Double): Unit = {
    uip(0) = u
    uip(1) = u
  }

  private[model] def initializeV(v: Double): Unit = {
    vjp(0) = v
    vjp(1) = v
  }

  def getU: Double = uip(current)
  def getV: Double = vjp(current)

  private[model] def setCurrentU(u: Double): Unit = {uip(current) = u}

  private[model] def setCurrentV(v: Double): Unit = {vjp(current) = v}
  private[model] def setNewU(newu: Double): Unit = {uip(1 - current) = newu}
  private[model] def setNewV(newv: Double): Unit = {vjp(1 - current) = newv}
  private[model] def incrementU(inc: Double): Unit = {uip(current) += inc}
  private[model] def incrementV(inc: Double): Unit = {vjp(current) += inc}

  def initialize(u: Double, v: Double): Unit = {
    uip(0) = u
    uip(1) = u
    vjp(0) = v
    vjp(1) = v
  }

  override def toString: String = {
    val sb = new StringBuilder("velocity=(")
    sb.append(uip(current)).append(", ").append(vjp(current)).append(")")
    sb.toString
  }
}
