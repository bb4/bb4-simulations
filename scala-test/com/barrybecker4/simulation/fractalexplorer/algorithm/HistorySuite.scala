// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.{ComplexNumber, ComplexNumberRange}
import org.scalatest.funsuite.AnyFunSuite

class HistorySuite extends AnyFunSuite {

  private def range(a: (Double, Double), b: (Double, Double)): ComplexNumberRange =
    ComplexNumberRange(ComplexNumber(a._1, a._2), ComplexNumber(b._1, b._2))

  test("pop returns most recently pushed range") {
    val h = new History()
    val r0 = range((0, 0), (1, 1))
    val r1 = range((2, 2), (3, 3))
    h.addRangeToHistory(r0)
    h.addRangeToHistory(r1)
    assert(h.popLastRange == r1)
    assert(h.popLastRange == r0)
    assert(!h.hasHistory)
  }

  test("goBack restores previous range") {
    val alg = new MandelbrotAlgorithm()
    val r0 = range((0, 0), (1, 1))
    val r1 = range((2, 2), (3, 3))
    alg.setRange(r0)
    alg.setRange(r1)
    alg.goBack()
    assert(alg.getRange == r0)
  }
}
