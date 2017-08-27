// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model.Cell
import com.barrybecker4.simulation.liquid.model.CellDimensions
import com.barrybecker4.simulation.liquid.model.CellNeighbors


/**
  * Ensures that mass is conserved while processing.
  * @param b0 relaxation coefficient.
  * @param dt delta time.
  * @author Barry Becker
  */
class MassConserver(var b0: Double, var dt: Double) {

  final private val dims = new CellDimensions

  /**
    * Update pressure and velocities to satisfy mass conservation.
    * What is the intuitive meaning of b0?
    * RISK:3
    * @return the amount of divergence from the cell that
    *         we will need to dissipate.
    */
  def updateMassConservation(cell: Cell, neighbors: CellNeighbors): Double = {
    if (!cell.isFull) return 0
    // divergence of fluid within the cell.
    val divergence = (neighbors.getLeft.getU - cell.getU) / dims.dx + (neighbors.getBottom.getV - cell.getV) / dims.dy
    val b = b0 / (dt * (2.0 / dims.dxSq + 2.0 / dims.dySq))
    // the change in pressure for a cell.
    val dp = b * divergence
    val dpdx = dt * dp / dims.dx
    val dpdy = dt * dp / dims.dy
    if (!neighbors.getRight.isObstacle) cell.incrementU(dpdx)
    if (!neighbors.getLeft.isObstacle) neighbors.getLeft.incrementU(-dpdx)
    if (!neighbors.getTop.isObstacle) cell.incrementV(dpdy)
    if (!neighbors.getBottom.isObstacle) neighbors.getBottom.incrementV(-dpdy)
    cell.setPressure(cell.getPressure + dp)
    Math.abs(divergence)
  }
}