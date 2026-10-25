// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import org.scalatest.funsuite.AnyFunSuite

class BenchmarkConfigSuite extends AnyFunSuite {

  test("no arguments gives the defaults") {
    val config = BenchmarkConfig.parse(Nil)
    assert(config.timeoutSeconds == 120)
    assert(config.repetitions == 3)
    assert(config.maxEvaluations == 40)
    assert(config.apps == Set("snake", "trebuchet"))
    assert(config.algorithms.isEmpty)
    assert(config.libVersion.isEmpty)
    assert(config.label.isEmpty)
    assert(config.resultsFile == "docs/benchmarks.json")
    assert(config.reportFile == "docs/benchmarks.md")
    assert(config.writeReport)
    assert(!config.inProcess)
  }

  test("every option can be overridden") {
    val config = BenchmarkConfig.parse(List(
      "--lib-version=2.0.0", "--label=2.0.0", "--timeout=30", "--reps=1", "--max-evals=5",
      "--apps=snake", "--algorithms=GENETIC_SEARCH", "--results=out/r.json", "--report=out/r.md",
      "--no-report", "--in-process"
    ))
    assert(config.libVersion.contains("2.0.0"))
    assert(config.label.contains("2.0.0"))
    assert(config.timeoutSeconds == 30)
    assert(config.repetitions == 1)
    assert(config.maxEvaluations == 5)
    assert(config.apps == Set("snake"))
    assert(config.algorithms == Set("GENETIC_SEARCH"))
    assert(config.resultsFile == "out/r.json")
    assert(config.reportFile == "out/r.md")
    assert(!config.writeReport)
    assert(config.inProcess)
  }

  test("bad options are rejected") {
    List("--timeout=abc", "--reps=0", "--max-evals=0", "--apps=fluid", "--bogus=1", "positional").foreach { bad =>
      intercept[IllegalArgumentException] {
        BenchmarkConfig.parse(List(bad))
      }
    }
  }
}
