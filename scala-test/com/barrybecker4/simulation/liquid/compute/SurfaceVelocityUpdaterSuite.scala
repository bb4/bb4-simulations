// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model._
import javax.vecmath.Vector2d
import com.barrybecker4.simulation.common.PhysicsConstants.ATMOSPHERIC_PRESSURE
import org.junit.Assert.assertEquals
import org.scalatest.funsuite.AnyFunSuite
import SurfaceVelocityUpdaterSuite._


/**
  * There are 13 surface conditions.
  * 1-4) One of the 4 immediate neighbors is empty
  * 5-8) Two of the 4 immediate neighbors is empty
  * 9-12) Three of the 4 immediate neighbors is empty
  * 13) isolated
  *
  * @author Barry Becker
  */
object SurfaceVelocityUpdaterSuite {
  private val EPS = 0.00001
  private val DIM = 6
  private val TOL = 0.0000000001
}

class SurfaceVelocityUpdaterSuite extends AnyFunSuite {

  /** instance under test. */
  private var svUpdater: SurfaceVelocityUpdater = _

  test("UpdateIsolatedInAbsenceOfFlow") {
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.EMPTY)
    val cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.ISOLATED)
    val neighbors = grid.getNeighbors(2, 2)
    svUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE)
    svUpdater.updateSurfaceVelocities(cell, neighbors)
    assertEquals("Unexpected pressure", ATMOSPHERIC_PRESSURE, cell.getPressure, TOL)
    verifyBorderVelocities(cell, neighbors, 0, 0, 0, 0)
  }

  test("UpdateIsolatedInNorthEastFlow") {
    val grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.EMPTY)
    val cell = grid.getCell(2, 2)
    cell.setStatus(CellStatus.ISOLATED)
    val neighbors = grid.getNeighbors(2, 2)
    svUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE)
    svUpdater.updateSurfaceVelocities(cell, neighbors)
    assertEquals("Unexpected pressure", ATMOSPHERIC_PRESSURE, cell.getPressure, TOL)
    verifyBorderVelocities(cell, neighbors, 0.285714285714, 0.28571428, 0.2054924, 0.205492455313)
  }

  private def verifyBorderVelocities(cell: Cell, neighbors: CellNeighbors, U: Double, V: Double, leftU: Double, bottomV: Double) = {
    val valid = Math.abs(U - cell.getU) < EPS &&
      Math.abs(V - cell.getV) < EPS &&
      Math.abs(leftU - neighbors.getLeft.getU) < EPS &&
      Math.abs(bottomV - neighbors.getBottom.getV) < EPS

    if (!valid) {
      System.out.println("U=" + cell.getU + " (expected " + U + ")")
      System.out.println("V=" + cell.getV + " (expected " + V + ")")
      System.out.println("leftU=" + neighbors.getLeft.getU + " (expected " + leftU + ")")
      System.out.println("bottomV=" + neighbors.getBottom.getV + " (expected " + bottomV + ")")
    }
    assert(valid,
      "Something out of range U=" + cell.getU + " V=" + cell.getV +
        " leftU=" + neighbors.getLeft.getU + " bottomV=" + neighbors.getBottom.getV)
  }
}

