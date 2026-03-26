// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumber
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite

class JuliaAlgorithmSuite extends AnyFunSuite {

  private val EPS = 1e-12
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  test("changing Julia seed changes escape time") {
    val j = new JuliaAlgorithm()
    j.setMaxIterations(50)
    val z = ComplexNumber(0.1, 0.2)
    j.setJuliaSeed(ComplexNumber(0, 0))
    val v0 = j.getFractalValue(z)
    j.setJuliaSeed(ComplexNumber(0.5, -0.3))
    val v1 = j.getFractalValue(z)
    assert(v0 != v1)
  }

  test("EscapeIteration matches Julia with seed") {
    val j = new JuliaAlgorithm()
    j.setMaxIterations(41)
    val seed = ComplexNumber(0.233, 0.5378)
    j.setJuliaSeed(seed)
    val z = ComplexNumber(0.05, -0.12)
    val direct = EscapeIteration.normalizedEscapeTime(z, seed, 41)
    assert(j.getFractalValue(z) === direct)
  }
}
