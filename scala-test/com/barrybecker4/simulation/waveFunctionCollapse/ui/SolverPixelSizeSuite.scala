// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.ui

import org.scalatest.funsuite.AnyFunSuite

import java.awt.Dimension

class SolverPixelSizeSuite extends AnyFunSuite {

  test("solverPixelSize leaves small panels unchanged") {
    val d = DynamicOptions.solverPixelSize(new Dimension(400, 300))
    assert(d.width == 400 && d.height == 300)
  }

  test("solverPixelSize caps large panels preserving aspect ratio") {
    val d = DynamicOptions.solverPixelSize(new Dimension(2000, 1000))
    assert(d.width == DynamicOptions.MAX_SOLVER_EDGE)
    assert(d.height == DynamicOptions.MAX_SOLVER_EDGE / 2)
  }

  test("solverPixelSize caps tall panels") {
    val d = DynamicOptions.solverPixelSize(new Dimension(100, 2000))
    assert(d.height == DynamicOptions.MAX_SOLVER_EDGE)
    assert(d.width == DynamicOptions.MAX_SOLVER_EDGE / 20)
  }
}
