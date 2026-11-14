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

  test("goBack with empty history is a no-op") {
    val alg = new MandelbrotAlgorithm()
    val initial = alg.getRange
    alg.goBack()
    assert(alg.getRange == initial)
  }

  test("goBack restores previous range with single pop") {
    val alg = new MandelbrotAlgorithm()
    val r0 = range((0, 0), (1, 1))
    val r1 = range((2, 2), (3, 3))
    alg.setRange(r0)
    alg.setRange(r1)
    alg.goBack()
    assert(alg.getRange == r0)
  }

  test("goBack after single zoom restores initial range") {
    val alg = new MandelbrotAlgorithm()
    val initial = alg.getRange
    val zoomed = range((-0.5, -0.5), (0.5, 0.5))
    alg.setRange(zoomed)
    alg.goBack()
    assert(alg.getRange == initial)
  }

  test("multi-zoom goBack walks back one level at a time") {
    val alg = new MandelbrotAlgorithm()
    val r0 = range((0, 0), (1, 1))
    val r1 = range((2, 2), (3, 3))
    val r2 = range((4, 4), (5, 5))
    alg.setRange(r0)
    alg.setRange(r1)
    alg.setRange(r2)
    alg.goBack()
    assert(alg.getRange == r1)
    alg.goBack()
    assert(alg.getRange == r0)
  }

  test("extra goBack after exhausting history is a no-op") {
    val alg = new MandelbrotAlgorithm()
    val r0 = range((0, 0), (1, 1))
    alg.setRange(r0)
    alg.goBack()
    val after = alg.getRange
    alg.goBack()
    assert(alg.getRange == after)
  }
}
