// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.habitat.model

import org.scalatest.funsuite.AnyFunSuite

import javax.vecmath.Point2d

class HabitatGridSuite extends AnyFunSuite {

  test("HabitatGrid construction") {

    val grid = HabitatGrid(0.1)

    assertResult(10) { grid.getXDim }
    assertResult(10) { grid.getYDim }
  }

  test("getCellForPosition") {
    val grid = HabitatGrid(0.1)

    val positions = Array(
      new Point2d(0.01, 0.95),
      new Point2d(0.15, 0.15),
      new Point2d(0.25, 0.899),
      new Point2d(0.5, 0.5),
      new Point2d(0, 0.9),
      new Point2d(0.78, 0.999)
    )

    assertResult(
        "Cell(0, 9), Cell(1, 1), Cell(2, 8), Cell(5, 5), Cell(0, 9), Cell(7, 9)"
    ) {
      positions.map(p => grid.getCellForPosition(p)).mkString(", ")
    }
  }

  test("getNeighborCells") {
    val grid = HabitatGrid(0.2)

    assertResult(
      "Cell(1, 1), Cell(1, 2), Cell(1, 3), Cell(2, 1), Cell(2, 3), Cell(3, 1), Cell(3, 2), Cell(3, 3), Cell(2, 2)"
    ) {
      grid.getNeighborCells(Cell(2, 2)).mkString(", ")
    }

    assertResult(
      "Cell(4, 4), Cell(4, 0), Cell(4, 1), Cell(0, 4), Cell(0, 1), Cell(1, 4), Cell(1, 0), Cell(1, 1), Cell(0, 0)"
    ) {
      grid.getNeighborCells(Cell(0, 0)).mkString(", ")
    }

    assertResult(
      "Cell(3, 3), Cell(3, 4), Cell(3, 0), Cell(4, 3), Cell(4, 0), Cell(0, 3), Cell(0, 4), Cell(0, 0), Cell(4, 4)"
    ) {
      grid.getNeighborCells(Cell(4, 4)).mkString(", ")
    }
  }
}
