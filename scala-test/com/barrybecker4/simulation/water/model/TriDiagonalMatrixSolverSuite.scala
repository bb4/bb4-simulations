package com.barrybecker4.simulation.water.model

import org.scalatest.funsuite.AnyFunSuite

class TriDiagonalMatrixSolverSuite extends AnyFunSuite {

  private val solver = TriDiagonalMatrixSolver(0.0001)

  /** 2×2 system matching this solver's (sub, main, super) layout; hand-verified solution x ≈ (0.6, -0.2). */
  test("solve two-row system with known solution") {
    val a = Array(Array(0.0, 2.0, 1.0), Array(3.0, 4.0, 0.0))
    val rhs = Array(1.0, 1.0)
    val x = Array(0.0, 0.0)

    solver.solve(a, rhs, x)

    assert(math.abs(x(0) - 0.6) < 1e-10)
    assert(math.abs(x(1) - (-0.2)) < 1e-10)
  }

  /** Very small main diagonal is lifted to `eps`, avoiding division by zero. */
  test("eps clamping keeps back substitution stable") {
    val eps = 1e-6
    val s = TriDiagonalMatrixSolver(eps)
    val a = Array(Array(0.0, 1e-12, 0.0), Array(0.0, 1.0, 0.0))
    val rhs = Array(1.0, 2.0)
    val x = Array(0.0, 0.0)
    s.solve(a, rhs, x)
    assert(x(0).isFinite)
    assert(x(1).isFinite)
  }
}
