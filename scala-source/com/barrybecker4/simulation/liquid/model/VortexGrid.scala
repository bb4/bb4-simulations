// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import javax.vecmath.Vector2d

import com.barrybecker4.simulation.liquid.model.CellStatus.CellStatus


/**
  * Create a grid with non-uniform velocity throughout.
  * The velocity will be set in the middle and taper off toward 0 at the edges.
  *
  * @author Barry Becker
  */
class VortexGrid private[model](xDim: Int, yDim: Int, val status: CellStatus) extends Grid(xDim, yDim) {

  val centerX: Double = xDim / 2
  val centerY: Double = yDim / 2

  for (j <- 1 until yDim - 1) {
    for (i <- 1 until xDim - 1) {
      val xDiff = centerX - i
      val yDiff = centerY - j
      val theta = Math.atan2(yDiff, xDiff)
      val tangentialVec = new Vector2d(-Math.sin(theta), Math.cos(theta))
      setVelocity(i, j, tangentialVec)
      getCell(i, j).setStatus(status)
    }
  }

  def this(xDim: Int, yDim: Int) {
    this(xDim, yDim, CellStatus.EMPTY)
  }
}
