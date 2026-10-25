// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import com.barrybecker4.simulation.snake.SnakeSimulator
import com.barrybecker4.simulation.trebuchet.TrebuchetParameters
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

  test("each app optimizes with the 2.1 winner and a fitness range on the scale of its objective") {
    assert(SnakeSimulator.SearchStrategy.toString == "SIMULATED_ANNEALING")
    assert(SnakeSimulator.FitnessRange == 10.0)
    assert(TrebuchetParameters.SearchStrategy.toString == "DIFFERENTIAL_EVOLUTION")
    assert(TrebuchetParameters.FitnessRange == 10000.0)
    assert(BenchmarkCases.fitnessRange(BenchmarkCases.SNAKE) == SnakeSimulator.FitnessRange)
    assert(BenchmarkCases.fitnessRange(BenchmarkCases.TREBUCHET) == TrebuchetParameters.FitnessRange)
  }
}
