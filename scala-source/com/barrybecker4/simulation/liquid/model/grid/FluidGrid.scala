// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model.grid

import com.barrybecker4.simulation.liquid.model.CellStatus

import javax.vecmath.Vector2d


class FluidGrid(xDim: Int, yDim: Int) extends Grid(xDim, yDim) {
  private val GRAVITY = new Vector2d(0.0, -9.81)

  // Initialize with just gravity and no artificial velocity field
  for (j <- 1 until yDim - 1) {
    for (i <- 1 until xDim - 1) {
      setVelocity(i, j, new Vector2d(0.0, 0.0))
      getCell(i, j).setStatus(CellStatus.EMPTY)
    }
  }

  override def setBoundaryConstraints(): Unit = {
    // No-slip boundary conditions
    for (j <- 0 until yDim) {
      // Left wall
      grid(0)(j).setPressure(grid(1)(j).getPressure)
      grid(0)(j).initializeVelocity(0.0, 0.0)

      // Right wall
      grid(xDim-1)(j).setPressure(grid(xDim - 2)(j).getPressure)
      grid(xDim-1)(j).initializeVelocity(0.0, 0.0)
    }

    for (i <- 0 until xDim) {
      // Bottom wall
      grid(i)(0).setPressure(grid(i)(1).getPressure)
      grid(i)(0).initializeVelocity(0.0, 0.0)

      // Top wall
      grid(i)(yDim-1).setPressure(grid(i)(yDim - 2).getPressure)
      grid(i)(yDim-1).initializeVelocity(0.0, 0.0)
    }
  }

  override def updateCellStatus(): Unit = {
    for (j <- 1 until yDim - 1) {
      for (i <- 1 until xDim - 1) {
        val cell = grid(i)(j)
        val neighbors = getNeighbors(i, j)

        // Update status considering free surface
        cell.updateStatus(neighbors)

        // Apply gravity to fluid cells
        if (cell.getStatus == CellStatus.FULL || cell.getStatus == CellStatus.ISOLATED) {
          val oldVel = new Vector2d(cell.getU, cell.getV)
          oldVel.add(GRAVITY)
          cell.initializeVelocity(oldVel.x, oldVel.y)
        }
      }
    }
  }
}