// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import org.scalatest.funsuite.AnyFunSuite

/**
  * Boundary conditions for the Stam-style grid.
  */
class FluidGridSuite extends AnyFunSuite {

  private def filledField(g: Grid): Array[Double] = {
    val x = new Array[Double](g.rowStride * (g.dimY + 2))
    for (i <- 1 to g.dimX; j <- 1 to g.dimY) x(g.idx(i, j)) = 10 * i + j
    x
  }

  test("setBoundary NEITHER copies orthogonal boundary values into padding") {
    val g = new Grid(3, 3)
    val x = filledField(g)
    g.setBoundary(Boundary.NEITHER, x)
    assert(x(g.idx(0, 2)) === x(g.idx(1, 2)))
    assert(x(g.idx(4, 2)) === x(g.idx(3, 2)))
    assert(x(g.idx(2, 0)) === x(g.idx(2, 1)))
    assert(x(g.idx(2, 4)) === x(g.idx(2, 3)))
  }

  test("setBoundary VERTICAL negates horizontal padding for vertical walls") {
    val g = new Grid(2, 2)
    val x = new Array[Double](g.rowStride * (g.dimY + 2))
    for (i <- 1 to 2; j <- 1 to 2) x(g.idx(i, j)) = i + j * 0.1
    g.setBoundary(Boundary.VERTICAL, x)
    assert(x(g.idx(0, 1)) === -x(g.idx(1, 1)))
    assert(x(g.idx(3, 1)) === -x(g.idx(2, 1)))
  }

  test("setBoundary HORIZONTAL negates vertical padding for horizontal walls") {
    val g = new Grid(2, 2)
    val x = new Array[Double](g.rowStride * (g.dimY + 2))
    for (i <- 1 to 2; j <- 1 to 2) x(g.idx(i, j)) = i + j * 0.1
    g.setBoundary(Boundary.HORIZONTAL, x)
    assert(x(g.idx(1, 0)) === -x(g.idx(1, 1)))
    assert(x(g.idx(1, 3)) === -x(g.idx(1, 2)))
  }
}
