// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.model

/**
  * Solves a tri-diagonal system of equations using a simplified form of Gaussian elimination
  * called the Thomas algorithm. See https://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm or
  * https://math.la.asu.edu/~gardner/tridiag.pdf
  *
  * '''Precondition:''' the coefficient matrix must be strictly diagonally dominant (as
  * `Environment.fillTridiagonalCoefficients` builds it). Under that condition pivots stay
  * safely away from zero; the `eps` guard is only a numerical safeguard for near-singular
  * general inputs and does not throw.
  *
  * Storage: row `i` has `(subDiagonal, mainDiagonal, superDiagonal)` as
  * `array(i)(0)`, `array(i)(1)`, `array(i)(2)` (boundary rows may leave an unused slot at 0 or 2).
  * @author Barry Becker
  */
case class TriDiagonalMatrixSolver(eps: Double = 0.0000001) {

  /** Solve for x in A * x = rhs.
    * Mutates both `array` (during elimination) and `rhs`. An empty system is a no-op.
    * @param array the tri-diagonal N×3 matrix
    * @param rhs right hand side vector
    * @param x the solution
    */
  def solve(array: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Unit = {
    if (array.length == 0) return
    forwardElimination(array, rhs, x)
    backSolve(array, rhs, x)
  }

  private def forwardElimination(array: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Unit = {
    val numRows = array.length
    for (i <- 0 until numRows - 1) {
      val ip = i + 1
      ensurePivot(array, i)
      val temp = array(ip)(0) / array(i)(1)
      rhs(ip) -= rhs(i) * temp
      array(ip)(1) -= array(i)(2) * temp
    }
  }

  private def backSolve(array: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Unit = {
    val numRows = array.length
    ensurePivot(array, numRows - 1)
    x(numRows - 1) = rhs(numRows - 1) / array(numRows - 1)(1)

    for (i <- numRows - 2 to 0 by -1) {
      val a = array(i)
      ensurePivot(array, i)
      x(i) = (rhs(i) - a(2) * x(i + 1)) / a(1)
    }
  }

  /** Replace a near-zero pivot with ±eps, preserving sign (avoids flipping legitimate negatives). */
  private def ensurePivot(array: Array[Array[Double]], i: Int): Unit = {
    val p = array(i)(1)
    if (math.abs(p) < eps)
      array(i)(1) = java.lang.Math.copySign(eps, p)
  }
}
