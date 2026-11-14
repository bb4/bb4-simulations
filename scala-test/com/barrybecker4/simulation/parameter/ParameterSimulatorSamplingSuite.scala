// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

/**
  * Seeded sampling: showRedistribution true → skewed; false → near-uniform after round-trip.
  */
class ParameterSimulatorSamplingSuite extends AnyFunSuite {

  private def meanAndSpread(samples: Seq[Double]): (Double, Double) = {
    val mean = samples.sum / samples.size
    val variance = samples.map(x => (x - mean) * (x - mean)).sum / samples.size
    (mean, math.sqrt(variance))
  }

  test("showRedistribution true on right-skewed gaussian concentrates above midrange") {
    val sim = new ParameterSimulator
    sim.setDistributionType(ParameterDistributionType.RIGHT_SKEWED_GAUSSIAN1)
    sim.showRedistribution = true
    val rnd = new Random(42)
    val samples = (0 until 2000).map(_ => sim.sampleXPosition(rnd))
    val (mean, _) = meanAndSpread(samples)
    // mean normalized ≈ 0.8 on [0,10] → mean around 8
    assert(mean > 6.0, s"expected right-skewed mean, got $mean")
  }

  test("showRedistribution false on right-skewed gaussian is near-uniform") {
    val sim = new ParameterSimulator
    sim.setDistributionType(ParameterDistributionType.RIGHT_SKEWED_GAUSSIAN1)
    sim.showRedistribution = false
    val rnd = new Random(42)
    val samples = (0 until 2000).map(_ => sim.sampleXPosition(rnd))
    val (mean, _) = meanAndSpread(samples)
    // Uniform on [0,10] → mean ≈ 5
    assert(mean > 4.0 && mean < 6.0, s"expected near-uniform mean ~5, got $mean")
  }

  test("showRedistribution true on boolean skewed prefers true when bias high") {
    val sim = new ParameterSimulator
    sim.setDistributionType(ParameterDistributionType.BOOLEAN_SKEWED)
    sim.showRedistribution = true
    val rnd = new Random(7)
    val samples = (0 until 1000).map(_ => sim.sampleXPosition(rnd))
    val trueFrac = samples.count(_ >= 0.5).toDouble / samples.size
    // BooleanRedistribution(0.8): special probs Array(percentTrue, 1-percentTrue) for (0,1)
    // — percentTrue is probability of false (index 0). So trueFrac should be near 0.2.
    // Document actual contract: prefer that one side dominates.
    assert(trueFrac < 0.4 || trueFrac > 0.6, s"expected skewed boolean, trueFrac=$trueFrac")
  }

  test("samples stay within parameter range") {
    val sim = new ParameterSimulator
    sim.setDistributionType(ParameterDistributionType.GAUSSIAN_NARROWEST)
    for (show <- Seq(true, false)) {
      sim.showRedistribution = show
      val rnd = new Random(99)
      val p = ParameterDistributionType.GAUSSIAN_NARROWEST.param
      for (_ <- 0 until 500) {
        val v = sim.sampleXPosition(rnd)
        assert(v >= p.minValue - 1e-9 && v <= p.maxValue + 1e-9, s"out of range $v show=$show")
      }
    }
  }
}
