// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import org.scalatest.funsuite.AnyFunSuite

class ParameterSimulatorHistogramSuite extends AnyFunSuite {

  test("double histogram uses fixed bin count") {
    assert(ParameterSimulator.numDoubleBins === 1000)
  }

  test("double histogram scale maps range to bin width") {
    assert(ParameterSimulator.doubleHistogramScale(5.0) === 200.0)
    assert(ParameterSimulator.doubleHistogramScale(1000.0) === 1.0)
  }

  test("double histogram offset negates min value for LinearFunction") {
    assert(ParameterSimulator.doubleHistogramOffset(0.0) === 0.0)
    assert(ParameterSimulator.doubleHistogramOffset(3.5) === -3.5)
  }

  test("double histogram scale rejects non-positive range") {
    intercept[IllegalArgumentException] {
      ParameterSimulator.doubleHistogramScale(0.0)
    }
    intercept[IllegalArgumentException] {
      ParameterSimulator.doubleHistogramScale(-1.0)
    }
  }

  test("integer preset uses range+1 bins for discrete uniform") {
    val p = ParameterDistributionType.DISCRETE_UNIFORM.param
    assert(p.isIntegerOnly)
    assert(p.range.toInt + 1 === ParameterDistributionType.NumDiscretes)
  }
}
