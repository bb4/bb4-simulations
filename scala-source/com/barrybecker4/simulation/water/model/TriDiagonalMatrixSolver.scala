// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.model

/**
  * Solves a tri-diagonal system of equations using a simplified form of Gaussian elimination
  * called the Thomas algorithm. See https://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm or
  * https://math.la.asu.edu/~gardner/tridiag.pdf
  *
  * Storage: row `i` has `(subDiagonal, mainDiagonal, superDiagonal)` as
  * `array(i)(0)`, `array(i)(1)`, `array(i)(2)` (boundary rows may leave an unused slot at 0 or 2).
  * @author Barry Becker
  */
case class TriDiagonalMatrixSolver(eps: Double = 0.0000001) {

  /** Solve for x in A * x = rhs (overwrites `rhs` during elimination).
    * @param array the tri-diagonal N×3 matrix
    * @param rhs right hand side vector
    * @param x the solution
    */
  def solve(array: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Unit = {
    forwardElimination(array, rhs, x)
    backSolve(array, rhs, x)
  }

  private def forwardElimination(array: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Unit = {
    val numRows = array.length
    for (i <- 0 until numRows - 1) {
      val ip = i + 1
      if (array(i)(1) < eps)
        array(i)(1) = eps
      val temp = array(ip)(0) / array(i)(1)
      rhs(ip) -= rhs(i) * temp
      array(ip)(1) -= array(i)(2) * temp
    }
  }

  private def backSolve(array: Array[Array[Double]], rhs: Array[Double], x: Array[Double]): Unit = {
    val numRows = array.length
    x(numRows - 1) = rhs(numRows - 1) / array(numRows - 1)(1)

    for (i <- numRows - 2 to 0 by -1) {
      val a = array(i)
      if (a(1) < eps)
        a(1) = eps
      x(i) = (rhs(i) - a(2) * x(i + 1)) / a(1)
    }
  }
}
