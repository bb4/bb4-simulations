// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import javax.vecmath.Vector2d
import com.barrybecker4.simulation.liquid.model.CellStatus


/**
  * Create a grid with non-uniform velocity throughout.
  * The velocity will be set in the middle and taper off toward 0 at the edges.
  * @author Barry Becker
  */
class NonUniformGrid(xDim: Int, yDim: Int, val velocity: Vector2d, val status: CellStatus)
  extends Grid(xDim, yDim) {

  val centerX: Double = xDim / 2 + 0.5
  val centerY: Double = yDim / 2 + 0.5
  val maxDist: Double = Math.sqrt(centerX * centerX + centerY * centerY)

  for (j <- 1 until yDim - 1) {
    for (i <- 1 until xDim - 1) {
      val xDiff = centerX - i
      val yDiff = centerY - j
      val dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff)
      val scale = (maxDist - dist) / maxDist
      val vel = new Vector2d(velocity.x * scale, velocity.y * scale)
      setVelocity(i, j, vel)
      this.getCell(i, j).setStatus(status)
    }
  }

  def this(xDim: Int, yDim: Int, velocity: Vector2d) = {
    this(xDim, yDim, velocity, CellStatus.EMPTY)
  }
}
