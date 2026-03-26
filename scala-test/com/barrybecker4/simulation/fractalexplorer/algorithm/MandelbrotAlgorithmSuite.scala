// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumber
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite

class MandelbrotAlgorithmSuite extends AnyFunSuite {

  private val EPS = 1e-12
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  test("origin iterates to max normalized value") {
    val m = new MandelbrotAlgorithm()
    m.setMaxIterations(20)
    val v = m.getFractalValue(ComplexNumber(0, 0))
    assert(v === 1.0)
  }

  test("quick escape gives low normalized value") {
    val m = new MandelbrotAlgorithm()
    m.setMaxIterations(100)
    val v = m.getFractalValue(ComplexNumber(2.5, 0))
    assert(v === 0.0)
  }

  test("EscapeIteration matches direct Mandelbrot") {
    val m = new MandelbrotAlgorithm()
    m.setMaxIterations(37)
    val c = ComplexNumber(-0.5, 0.25)
    val direct = EscapeIteration.normalizedEscapeTime(c, c, 37)
    assert(m.getFractalValue(c) === direct)
  }
}
