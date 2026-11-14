// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.common.geometry.{Box, IntLocation}
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite

class FractalAlgorithmSuite extends AnyFunSuite {

  private val EPS = 1e-9
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  test("getComplexPosition maps corners to range endpoints") {
    val alg = new MandelbrotAlgorithm()
    alg.setSize(100, 50)
    val range = alg.getRange
    val topLeft = alg.getComplexPosition(0, 0)
    assert(topLeft.real === range.point1.real)
    assert(topLeft.imaginary === range.point1.imaginary)

    // Last pixel index maps to ratio (w-1)/w, (h-1)/h — not exactly point2
    val nearBottomRight = alg.getComplexPosition(99, 49)
    assert(nearBottomRight.real === range.point1.real + (range.point2.real - range.point1.real) * 0.99)
    assert(nearBottomRight.imaginary === range.point1.imaginary + (range.point2.imaginary - range.point1.imaginary) * 0.98)
  }

  test("getRange from full-model box recovers algorithm range corners") {
    val alg = new MandelbrotAlgorithm()
    alg.setSize(200, 100)
    val box = new Box(IntLocation(0, 0), IntLocation(100, 200))
    val mapped = alg.getRange(box)
    val expected = alg.getRange
    assert(mapped.point1.real === expected.point1.real)
    assert(mapped.point1.imaginary === expected.point1.imaginary)
    assert(mapped.point2.real === expected.point2.real)
    assert(mapped.point2.imaginary === expected.point2.imaginary)
  }

  test("timeStep advances rows and eventually finishes") {
    val alg = new MandelbrotAlgorithm()
    alg.setSize(16, 16)
    alg.setParallelized(false)
    alg.setMaxIterations(20)
    assert(!alg.getModel.isDone)
    assert(alg.getModel.getCurrentRow == 0)

    val doneQuickly = alg.timeStep(4.0)
    assert(!doneQuickly)
    assert(alg.getModel.getCurrentRow > 0)

    var steps = 0
    while (!alg.getModel.isDone && steps < 100) {
      alg.timeStep(8.0)
      steps += 1
    }
    assert(alg.getModel.isDone, s"expected done after stepping, row=${alg.getModel.getCurrentRow}")
    assert(alg.timeStep(1.0))
  }

  test("setMaxIterations resets computation to row 0") {
    val alg = new MandelbrotAlgorithm()
    alg.setSize(20, 20)
    alg.setParallelized(false)
    alg.timeStep(5.0)
    assert(alg.getModel.getCurrentRow > 0)
    alg.setMaxIterations(alg.getMaxIterations + 1)
    assert(alg.getModel.getCurrentRow == 0)
  }
}
