package com.barrybecker4.simulation.water.model

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class TriDiagonalMatrixSolverSuite extends AnyFunSuite {

  private val solver = TriDiagonalMatrixSolver(0.0001)

  /** Multiply tri-diagonal `a` (sub, main, super) by `x` into a dense residual vs `rhs`. */
  private def maxResidual(a: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Double = {
    val n = a.length
    var max = 0.0
    for (i <- 0 until n) {
      var ax = a(i)(1) * x(i)
      if (i > 0) ax += a(i)(0) * x(i - 1)
      if (i < n - 1) ax += a(i)(2) * x(i + 1)
      max = math.max(max, math.abs(ax - rhs(i)))
    }
    max
  }

  private def copyMatrix(a: Array[Array[Double]]): Array[Array[Double]] =
    a.map(_.clone())

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

  test("solve leaves residual near zero for diagonally dominant systems") {
    val rng = new Random(42)
    for (n <- Seq(1, 2, 5, 20)) {
      val a = Array.fill(n, 3)(0.0)
      val rhs = Array.fill(n)(0.0)
      for (i <- 0 until n) {
        val sub = if (i == 0) 0.0 else rng.nextDouble() * 2.0
        val superDiag = if (i == n - 1) 0.0 else rng.nextDouble() * 2.0
        a(i)(0) = -sub
        a(i)(2) = -superDiag
        a(i)(1) = 1.0 + sub + superDiag + rng.nextDouble()
        rhs(i) = rng.nextDouble() * 10.0 - 5.0
      }
      val aCopy = copyMatrix(a)
      val rhsCopy = rhs.clone()
      val x = Array.fill(n)(0.0)
      solver.solve(a, rhs, x)
      assert(maxResidual(aCopy, rhsCopy, x) < 1e-8, s"n=$n")
    }
  }
}
