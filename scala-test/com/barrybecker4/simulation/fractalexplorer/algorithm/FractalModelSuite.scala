// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer.algorithm

import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite

class FractalModelSuite extends AnyFunSuite {

  private val EPS = 1e-12
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  test("aspect ratio uses floating division") {
    val m = new FractalModel()
    m.setSize(800, 600)
    assert(m.getAspectRatio === (800.0 / 600.0))
  }

  test("getValue out of bounds returns zero") {
    val m = new FractalModel()
    m.setSize(10, 10)
    assert(m.getValue(-1, 0) == 0)
    assert(m.getValue(10, 0) == 0)
    assert(m.getValue(0, -1) == 0)
    assert(m.getValue(0, 10) == 0)
  }

  test("setValue getValue round trip in bounds") {
    val m = new FractalModel()
    m.setSize(5, 5)
    m.setValue(2, 3, 0.42)
    assert(m.getValue(2, 3) === 0.42)
  }
}
