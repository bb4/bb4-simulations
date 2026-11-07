// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import com.barrybecker4.simulation.benchmark.BenchmarkTstUtil.{cell, runRecord}
import org.scalatest.funsuite.AnyFunSuite

class ReportGeneratorSuite extends AnyFunSuite {

  test("a finished cell shows median fitness, median time, and how many runs finished") {
    assert(ReportGenerator.cellText(cell("GENETIC_SEARCH"), 120) == "3.14 (1.20 s, 3/3)")
    assert(ReportGenerator.cellText(cell("GENETIC_SEARCH", finished = 1, attempts = 3, medianFitness = Some(-9881.4)), 120) ==
      "-9881 (1.20 s, 1/3)")
  }

  test("timeouts and errors are named") {
    assert(ReportGenerator.cellText(cell("HILL_CLIMBING", finished = 0, timedOut = 2, attempts = 2, medianFitness = None, medianMillis = None), 120) ==
      "timed out after 120s")
    val crashed = cell("GENETIC_SEARCH", finished = 0, errored = 2, attempts = 2, medianFitness = None, medianMillis = None,
      lastError = Some("java.lang.IllegalStateException: boom | next\nline"))
    val text = ReportGenerator.cellText(crashed, 120)
    assert(text.startsWith("error: java.lang.IllegalStateException: boom / next line"))
    assert(!text.contains("|"))
    assert(!text.contains("\n"))
  }

  test("a missing strategy in an older column is n/a and the comparison uses fitness and time") {
    val baseline = runRecord("2.0.0", List(
      cell("GENETIC_SEARCH", medianFitness = Some(6.0), medianMillis = Some(2000))
    ))
    val newer = runRecord("2.1-SNAPSHOT", List(
      cell("GENETIC_SEARCH", medianFitness = Some(3.0), medianMillis = Some(1000)),
      cell("PARTICLE_SWARM", medianFitness = Some(2.5), medianMillis = Some(800))
    ), libVersion = "2.1-SNAPSHOT")
    val report = ReportGenerator.render(List(baseline, newer))
    assert(report.contains("./gradlew benchmark"))
    assert(!report.contains("benchmarkReport"))
    assert(report.contains("| App | Algorithm | bb4-optimization 2.0.0 | bb4-optimization 2.1-SNAPSHOT | 2.1-SNAPSHOT vs 2.0.0 |"))
    val genetic = report.linesIterator.find(_.contains("`GENETIC_SEARCH`")).get
    assert(genetic.contains("6.00 (2.00 s, 3/3)"))
    assert(genetic.contains("3.00 (1.00 s, 3/3)"))
    assert(genetic.contains("2.0x better fitness"))
    assert(genetic.contains("2.0x faster"))
    val swarm = report.linesIterator.find(_.contains("`PARTICLE_SWARM`")).get
    assert(swarm.contains(ReportGenerator.NOT_AVAILABLE))
    assert(swarm.contains("2.50 (800 ms, 3/3)"))
  }

  test("a longer trebuchet throw is better fitness") {
    val change = ReportGenerator.changeText(
      Some(cell("GENETIC_SEARCH", app = "Trebuchet", medianFitness = Some(-1000), medianMillis = Some(1000))),
      Some(cell("GENETIC_SEARCH", app = "Trebuchet", medianFitness = Some(-2000), medianMillis = Some(1000)))
    )
    assert(change.contains("2.0x better fitness"))
  }

  test("an empty report tells you to run the benchmark task") {
    val report = ReportGenerator.render(Nil)
    assert(report.contains("No results have been recorded yet"))
    assert(report.contains("./gradlew benchmark"))
  }
}
