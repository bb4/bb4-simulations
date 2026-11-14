// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.optimization.parameter.types.{DoubleParameter, IntegerParameter}
import org.scalatest.funsuite.AnyFunSuite

class ParameterSimulatorHistogramSuite extends AnyFunSuite {

  test("double histogram uses fixed bin count") {
    assert(ParameterSimulator.numDoubleBins === 1000)
  }

  test("double histogram scale maps range to bin width") {
    assert(ParameterSimulator.doubleHistogramScale(5.0) === 200.0)
    assert(ParameterSimulator.doubleHistogramScale(1000.0) === 1.0)
  }

  test("double histogram offset is -scale * min for LinearFunction a*x+b") {
    assert(ParameterSimulator.doubleHistogramOffset(0.0, 200.0) === 0.0)
    assert(ParameterSimulator.doubleHistogramOffset(3.5, 200.0) === -700.0)
  }

  test("double histogram scale rejects non-positive range") {
    intercept[IllegalArgumentException] {
      ParameterSimulator.doubleHistogramScale(0.0)
    }
    intercept[IllegalArgumentException] {
      ParameterSimulator.doubleHistogramScale(-1.0)
    }
  }

  test("double histogram function maps min to ~0 and max to ~numBins when min != 0") {
    val f = ParameterSimulator.doubleHistogramFunction(3.5, 8.5)
    assert(math.abs(f.getValue(3.5)) < 1e-9)
    assert(math.abs(f.getValue(8.5) - ParameterSimulator.numDoubleBins) < 1e-6)
    assert(math.abs(f.getValue(6.0) - ParameterSimulator.numDoubleBins / 2.0) < 1e-6)
  }

  test("integer preset uses range+1 bins for discrete uniform") {
    val p = ParameterDistributionType.DISCRETE_UNIFORM.param
    assert(p.isIntegerOnly)
    assert(p.range.toInt + 1 === ParameterDistributionType.NumDiscretes)
  }

  test("setParameter switches integer vs double histogram bin counts") {
    val sim = new ParameterSimulator
    sim.setParameter(new IntegerParameter(0, 0, 9, "i"))
    assert(sim.histogramBinCount === 10)

    sim.setParameter(new DoubleParameter(0, 0, 5.0, "d"))
    assert(sim.histogramBinCount === ParameterSimulator.numDoubleBins)
  }

  test("integer histogram supports non-zero min via offset mapping") {
    val sim = new ParameterSimulator
    sim.setParameter(new IntegerParameter(5, 5, 10, "i"))
    assert(sim.histogramBinCount === 6) // range 5 → 6 bins
  }
}
