// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import com.barrybecker4.simulation.common.PhysicsConstants.ATMOSPHERIC_PRESSURE
import com.barrybecker4.simulation.liquid.model.CellStatus.CellStatus


/**
  * A region of space containing particles, walls, or liquid.
  * Adapted from work by Nick Foster.
  *
  *         &#94;  vjp_[cur_]     (positive v direction)
  *           |
  *     _____________
  *     |            |
  * &lt;--  |      p     |  --&gt; uip_[cur_]   (positive u dir)
  *     |            |
  *     ------------
  *           |
  *           v
  *
  *     |--- dx ---|
  *
  * @author Barry Becker
  */
class Cell() {
  private var pressure = ATMOSPHERIC_PRESSURE // pressure at the center of the cell

  /** velocities in x, y directions defined at the center of each face */
  private val velocity = new CellVelocity
  private var numParticles = 0  //  Num particles in this cell
  private var status = CellStatus.EMPTY // type of cell

  /** global swap of fields (use with care). (hack) */
  def swap() { velocity.step() }
  def setPressure(p: Double) { pressure = p }
  def getPressure: Double = pressure
  private[model] def initializeU(u: Double): Unit = {velocity.initializeU(u)}
  private[model] def initializeV(v: Double): Unit = {velocity.initializeV(v)}

  def initializeVelocity(u: Double, v: Double) { velocity.initialize(u, v)}
  def setU(u: Double) {velocity.setCurrentU(u)}
  def setV(v: Double): Unit = {velocity.setCurrentV(v)}
  def getU: Double = velocity.getU
  def getV: Double = velocity.getV

  def incrementU(inc: Double): Unit = {velocity.incrementU(inc)}
  def incrementV(inc: Double): Unit = {velocity.incrementV(inc)}

  def setNewU(u: Double) {velocity.setNewU(u)}
  def setNewV(v: Double) {velocity.setNewV(v)}

  def passVelocityThrough() {velocity.passThrough()}
  def incParticles() { numParticles += 1 }

  def decParticles() {
    numParticles -= 1
    assert(numParticles >= 0)
  }

  def getNumParticles: Int = numParticles
  def getStatus: CellStatus = status
  def isSurface: Boolean = status eq CellStatus.SURFACE
  def isEmpty: Boolean = status eq CellStatus.EMPTY
  def isFull: Boolean = status eq CellStatus.FULL
  def isObstacle: Boolean = status eq CellStatus.OBSTACLE
  def isIsolated: Boolean = status eq CellStatus.ISOLATED
  def setStatus(status: CellStatus): Unit = {this.status = status}

  /**
    * Compute the cell's new status based on numParticles inside and
    * the status of neighbors.
    * RISK: 1
    */
  def updateStatus(neighbors: CellNeighbors): Unit = {
    assert(numParticles >= 0, "num particles less than 0.")
    if (status eq CellStatus.OBSTACLE) {
      // obstacles never change status
    }
    else if (numParticles == 0) status = CellStatus.EMPTY
    else if (neighbors.allHaveParticles) status = CellStatus.FULL
    else if (neighbors.noneHaveParticles) { // warning: not present in original foster code.
      status = CellStatus.ISOLATED
    }
    else status = CellStatus.SURFACE
  }

  override def toString: String = {
    val sb = new StringBuilder("Cell:")
    sb.append(status)
    sb.append(" num particles=").append(numParticles)
    sb.append(" pressure=").append(pressure)
    sb.append(" ").append(velocity)
    sb.toString
  }
}
