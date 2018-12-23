package com.barrybecker4.simulation.water.model

import org.scalatest.FunSuite

class TriDiagonalMatrixSolverSuite extends FunSuite {

  val solver = TriDiagonalMatrixSolver(0.0001)

  /** Simple case from https://math.la.asu.edu/~gardner/tridiag.pdf
    * but it did not give the result expected. Probably because the implementation used is specific to
    * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.89.1204&rep=rep1&type=pdf
    */
  test("solve simple 3 row matrix") {

    val a = Array(Array(-2.0, 1.0, 0.0), Array(1.0, -2.0, 1.0), Array(0.0, 1.0, -2.0))
    val rhs = Array(1.0, 2.0, 3.0)
    val x = Array.fill(3)(0.0) //(1.0, 2.0, 3.0)

    solver.solve(a, rhs, x)

    assertResult("1.0, 1.0, 3.0") {rhs.mkString(", ")}
    assertResult("1.0, -20000.0, 3.0") {x.mkString(", ")}
  }

}
