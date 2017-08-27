// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model.Grid


/**
  * Iterates, conserving mass, until the pressure stabilizes.
  * @author Barry Becker
  */
object PressureUpdater {
  private val EPSILON = 0.000000001
}

/**
  * @param grid the grid of cells that make up the environment
  * @param b0 pressure constant?
  */
class PressureUpdater(var grid: Grid, var b0: Double) {

  private var iterationCount = 0

  /** @return the number of iterations it took to converge */
  def getNumIterations: Int = iterationCount

  /**
    * perform pressure iteration to consider mass conservation.
    * repeat till all cells in the flow field have a divergence less than EPSILON.
    * When things go bad, this can take 50-70 or more iterations.
    * RISK: 6
    * @return the maximum divergence of any of the cells in the grid.
    */
  def updatePressure(timeStep: Double): Double = {
    var maxDivergence = .0
    var divergence = .0
    val conserver = new MassConserver(b0, timeStep)
    do { // adjust tilde velocities to satisfy mass conservation
      maxDivergence = 0
      for (j <- 1 until grid.getYDimension - 1) {
        for (i <- 1 until grid.getXDimension - 1) {
          divergence = conserver.updateMassConservation(grid.getCell(i, j), grid.getNeighbors(i, j))
          if (divergence > maxDivergence) maxDivergence = divergence
        }
      }
      iterationCount += 1
    } while (maxDivergence > PressureUpdater.EPSILON)
    maxDivergence
  }
}
