// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import org.scalatest.funsuite.AnyFunSuite

class BenchmarkCasesSuite extends AnyFunSuite {

  test("the catalog is every strategy that applies to a bounded continuous problem") {
    assert(BenchmarkCases.Strategies == List(
      "GLOBAL_SAMPLING",
      "GLOBAL_HILL_CLIMBING",
      "GLOBAL_SIMULATED_ANNEALING",
      "HILL_CLIMBING",
      "SIMULATED_ANNEALING",
      "GENETIC_SEARCH",
      "CONCURRENT_GENETIC_SEARCH",
      "DIFFERENTIAL_EVOLUTION",
      "PARTICLE_SWARM",
      "ITERATED_LOCAL_SEARCH",
      "VARIABLE_NEIGHBORHOOD_SEARCH"
    ))
  }
}
