// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumber
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite

class EscapeIterationSuite extends AnyFunSuite {

  private val EPS = 1e-12
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  test("interior origin reaches max iterations → 1.0") {
    val v = EscapeIteration.normalizedEscapeTime(ComplexNumber(0, 0), ComplexNumber(0, 0), 25)
    assert(v === 1.0)
  }

  test("point already outside radius escapes with 0 iterations → 0.0") {
    val v = EscapeIteration.normalizedEscapeTime(ComplexNumber(2.0, 0), ComplexNumber(0, 0), 100)
    assert(v === 0.0)
  }

  test("maxIterations = 1: non-escaping seed yields 1.0") {
    val v = EscapeIteration.normalizedEscapeTime(ComplexNumber(0, 0), ComplexNumber(0, 0), 1)
    assert(v === 1.0)
  }

  test("maxIterations = 1: already escaped seed yields 0.0") {
    val v = EscapeIteration.normalizedEscapeTime(ComplexNumber(3, 0), ComplexNumber(0, 0), 1)
    assert(v === 0.0)
  }

  test("quick escape far from set is low normalized value") {
    val v = EscapeIteration.normalizedEscapeTime(ComplexNumber(2.5, 0), ComplexNumber(2.5, 0), 100)
    assert(v === 0.0)
  }

  test("|z| exactly 2 does not enter loop → 0.0") {
    // Escape when mag² >= 4, so |z| == 2 is already escaped.
    val v = EscapeIteration.normalizedEscapeTime(ComplexNumber(2.0, 0), ComplexNumber(-1, 0), 50)
    assert(v === 0.0)
  }
}
