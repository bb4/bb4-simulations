// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.compute

import com.barrybecker4.simulation.liquid.model._
import javax.vecmath.Vector2d
import org.junit.Assert.assertEquals
import org.scalatest.FunSuite
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

class PressureUpdaterSuite extends FunSuite {
  /** instance under test. */
  private var pressureUpdater: PressureUpdater = _

  test("PressureUpdateUniform") {
    val b0 = 1.0
    val grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0), CellStatus.FULL)
    pressureUpdater = new PressureUpdater(grid, b0)
    val maxDiv = pressureUpdater.updatePressure(DT)
    assertEquals("Unexpected divergence", 0.0, maxDiv, TOL)
    assertEquals("Unexpected number of iterations till convergence", 2, pressureUpdater.getNumIterations)
    val cell1 = grid.getCell(1, 1)
    verifyCell(cell1, -49.1, new Vector2d(0.5, 0.5))
    val cell2 = grid.getCell(1, 2)
    verifyCell(cell2, 0.9, new Vector2d(1.0, 1.0))
  }

  /** Currently failing */
  test("PressureUpdateNonUniform") {
    val b0 = 1.0
    val grid = new NonUniformGrid(DIM, DIM, new Vector2d(1.0, 1.0), CellStatus.FULL)
    pressureUpdater = new PressureUpdater(grid, b0)
    val maxDiv = pressureUpdater.updatePressure(DT)
    assertEquals("Unexpected divergence", 0.0, maxDiv, 0.000000001)
    assertEquals("Unexpected number of iterations till convergence", 45, pressureUpdater.getNumIterations)
    /*
            Cell cell1 = grid.getCell(1, 1);
            verifyCell(cell1, -13.385714285714284, new Vector2d(0.14285714285714285, 0.14285714285714285));
            Cell cell2 = grid.getCell(1, 2);
            verifyCell(cell2, 0.9, new Vector2d(0.49120674102731, 0.41098491062604847));
            */
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
    assertEquals("Unexpected divergence", 2.7755575615628914E-17, maxDiv, TOL)
    assertEquals("Unexpected number of iterations till convergence", 2, pressureUpdater.getNumIterations)
    System.out.println(grid.toString)
    val cell1 = grid.getCell(1, 1)
    verifyCell(cell1, 0.9, new Vector2d(0.0, 0.0))
    val cell2 = grid.getCell(1, 2)
    verifyCell(cell2, 0.9, new Vector2d(0.0, 0.0))
  }

  private def verifyCell(cell: Cell, pressure: Double, expUV: Vector2d) = {
    assertEquals("Unexpected pressure", pressure, cell.getPressure, TOL)
    assertEquals("Unexpected velocity", expUV, new Vector2d(cell.getU, cell.getV))
  }

  private def verifyPressures(grid: Grid, expPressure: Double) = {
    for (i <- 1 until grid.getXDimension - 1)
      for (j <- 1 until grid.getYDimension - 1) {
        val cell = grid.getCell(i, j)
        assertEquals("Unexpected pressure at " + cell, expPressure, cell.getPressure, TOL)
      }
  }
}

