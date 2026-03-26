// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model._
import javax.vecmath.Vector2d
import org.junit.jupiter.api.Assertions.assertEquals
import org.scalatest.funsuite.AnyFunSuite
import PressureUpdaterSuite._


/**
  * @author Barry Becker
  */
object PressureUpdaterSuite {
  /** delta time */
  private val DT = 0.1
  private val DIM = 6
  private val TOL = 0.0000000001
}

class PressureUpdaterSuite extends AnyFunSuite {
  /** instance under test. */
  private var pressureUpdater: PressureUpdater = _

  test("PressureUpdateUniform") {
    val b0 = 1.0
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0), CellStatus.FULL)
    pressureUpdater = new PressureUpdater(grid, b0)
    val maxDiv = pressureUpdater.updatePressure(DT)
    assertEquals(0.0, maxDiv, TOL, "Unexpected divergence")
    assertEquals(2, pressureUpdater.getNumIterations, "Unexpected number of iterations till convergence")
    val cell1 = grid.getCell(1, 1)
    verifyCell(cell1, -49.1, new Vector2d(0.5, 0.5))
    val cell2 = grid.getCell(1, 2)
    verifyCell(cell2, 0.9, new Vector2d(1.0, 1.0))
  }

  test("PressureUpdateNonUniform") {
    val b0 = 1.0
    val grid = new NonUniformGrid(DIM, DIM, new Vector2d(1.0, 1.0), CellStatus.FULL)
    pressureUpdater = new PressureUpdater(grid, b0)
    val maxDiv = pressureUpdater.updatePressure(DT)
    assertEquals(0.0, maxDiv, 0.000000001, "Unexpected divergence")
    assertEquals(45, pressureUpdater.getNumIterations, "Unexpected number of iterations till convergence")
  }

  test("PressureUpdateStopsAtMaxIterationsWhenNotConverged") {
    val b0 = 1.0
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0), CellStatus.FULL)
    val pressureUpdater = new PressureUpdater(grid, b0, maxIterations = 1)
    val maxDiv = pressureUpdater.updatePressure(DT)
    assert(pressureUpdater.getStoppedByIterationCap, "expected iteration cap to stop before convergence")
    assert(maxDiv > PressureUpdater.EPSILON, "divergence should remain above epsilon with cap of 1")
    assertEquals(1, pressureUpdater.getNumIterations, "Unexpected iteration count")
  }

  test("PressureUpdateRandom") {
    val b0 = 1.0
    val grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.FULL)
    // insert som wildly varying vectors
    val cell22 = grid.getCell(2, 2)
    val cell33 = grid.getCell(3, 3)
    cell22.setU(-0.5)
    cell22.setV(0.5)
    cell33.setU(1.5)
    cell33.setV(-0.8)
    pressureUpdater = new PressureUpdater(grid, b0)
    val maxDiv = pressureUpdater.updatePressure(DT)
    assertEquals(2.7755575615628914E-17, maxDiv, TOL, "Unexpected divergence")
    assertEquals(2, pressureUpdater.getNumIterations, "Unexpected number of iterations till convergence")
    val cell1 = grid.getCell(1, 1)
    verifyCell(cell1, 0.9, new Vector2d(0.0, 0.0))
    val cell2 = grid.getCell(1, 2)
    verifyCell(cell2, 0.9, new Vector2d(0.0, 0.0))
  }

  private def verifyCell(cell: Cell, pressure: Double, expUV: Vector2d) = {
    assertEquals(pressure, cell.getPressure, TOL, "Unexpected pressure")
    assertEquals(expUV, new Vector2d(cell.getU, cell.getV), "Unexpected velocity")
  }
}

