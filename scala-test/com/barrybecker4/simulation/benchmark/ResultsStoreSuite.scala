// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import com.barrybecker4.simulation.benchmark.BenchmarkTstUtil.{cell, runRecord}
import com.barrybecker4.simulation.benchmark.Json.*
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.Files

class ResultsStoreSuite extends AnyFunSuite {

  test("runs survive a round trip through JSON") {
    val runs = List(
      runRecord("2.0.0", List(cell("GENETIC_SEARCH"))),
      runRecord("2.1-SNAPSHOT+options", List(cell("PARTICLE_SWARM", medianFitness = Some(2.5))), libVersion = "2.1-SNAPSHOT")
    )
    assert(ResultsStore.fromJson(Json.parse(Json.write(ResultsStore.toJson(runs)))) == runs)
  }

  test("re-running a label replaces that column and leaves the others") {
    val baseline = runRecord("2.0.0", List(cell("GENETIC_SEARCH", medianFitness = Some(4.0))))
    val again = runRecord("2.0.0", List(cell("GENETIC_SEARCH", medianFitness = Some(3.5))))
    val other = runRecord("2.1-SNAPSHOT", List(cell("GENETIC_SEARCH", medianFitness = Some(3.0))), libVersion = "2.1-SNAPSHOT")
    val replaced = ResultsStore.upsert(ResultsStore.upsert(List(baseline), other), again)
    assert(replaced.map(_.label) == List("2.0.0", "2.1-SNAPSHOT"))
    assert(replaced.head.results.head.medianFitness.contains(3.5))
    assert(replaced.last.results.head.medianFitness.contains(3.0))
  }

  test("a file without a label uses the library version as the column name") {
    def dropLabel(json: Json): Json = json match {
      case JObj(fields) => JObj(fields.filterNot(_._1 == "label").map { case (k, v) => (k, dropLabel(v)) })
      case JArr(items) => JArr(items.map(dropLabel))
      case other => other
    }
    val loaded = ResultsStore.fromJson(dropLabel(ResultsStore.toJson(List(runRecord("2.0.0", List(cell("HILL_CLIMBING")))))))
    assert(loaded.head.label == "2.0.0")
  }

  test("saving and loading a file round-trips") {
    val dir = Files.createTempDirectory("sim-bench")
    val file = dir.resolve("nested").resolve("benchmarks.json").toString
    val runs = List(runRecord("2.0.0", List(cell("SIMULATED_ANNEALING", medianFitness = Some(-1200.0), app = "Trebuchet"))))
    ResultsStore.save(file, runs)
    assert(ResultsStore.load(file) == runs)
    assert(ResultsStore.load(dir.resolve("missing.json").toString).isEmpty)
  }
}
