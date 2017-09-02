// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import javax.vecmath.Vector2d
import com.barrybecker4.simulation.liquid.model.CellStatus.CellStatus


/**
  * Create a grid with uniform velocity at each cell.
  * @author Barry Becker
  */
class UniformGrid(xDim: Int, yDim: Int, val velocity: Vector2d, val status: CellStatus) extends Grid(xDim, yDim) {

  for (j <- 1 until yDim -1) {
    for (i <- 1 until xDim - 1) {
      setVelocity(i, j, velocity)
      this.getCell(i, i).setStatus(status)
    }
  }

  def this(xDim: Int, yDim: Int, velocity: Vector2d) {
    this(xDim, yDim, velocity, CellStatus.EMPTY)
  }
}
