// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import org.scalatest.funsuite.AnyFunSuite

/**
  * Boundary conditions for the Stam-style grid.
  */
class FluidGridSuite extends AnyFunSuite {

  test("setBoundary NEITHER copies orthogonal boundary values into padding") {
    val g = new Grid(3, 3)
    val x = Array.ofDim[Double](5, 5)
    for (i <- 1 to 3; j <- 1 to 3) x(i)(j) = 10 * i + j
    g.setBoundary(Boundary.NEITHER, x)
    assert(x(0)(2) === x(1)(2))
    assert(x(4)(2) === x(3)(2))
    assert(x(2)(0) === x(2)(1))
    assert(x(2)(4) === x(2)(3))
  }

  test("setBoundary VERTICAL negates horizontal padding for vertical walls") {
    val g = new Grid(2, 2)
    val x = Array.ofDim[Double](4, 4)
    for (i <- 1 to 2; j <- 1 to 2) x(i)(j) = i + j * 0.1
    g.setBoundary(Boundary.VERTICAL, x)
    assert(x(0)(1) === -x(1)(1))
    assert(x(3)(1) === -x(2)(1))
  }

  test("setBoundary HORIZONTAL negates vertical padding for horizontal walls") {
    val g = new Grid(2, 2)
    val x = Array.ofDim[Double](4, 4)
    for (i <- 1 to 2; j <- 1 to 2) x(i)(j) = i + j * 0.1
    g.setBoundary(Boundary.HORIZONTAL, x)
    assert(x(1)(0) === -x(1)(1))
    assert(x(1)(3) === -x(1)(2))
  }
}
