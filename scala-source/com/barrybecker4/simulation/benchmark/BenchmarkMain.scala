// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.time.Instant

/**
  * Benchmarks the continuous bb4-optimization strategies on Snake and Trebuchet and records the results
  * under a column label. Run it with `./gradlew benchmark`.
  *
  * Re-running the same label replaces that column. A different label adds a column.
  */
object BenchmarkMain {

  def main(args: Array[String]): Unit = {
    val status =
      try {
        run(BenchmarkConfig.parse(args.toList))
        0
      } catch {
        case e: IllegalArgumentException =>
          Console.err.println(e.getMessage)
          2
        case e: IllegalStateException =>
          Console.err.println(e.getMessage)
          2
      }
    System.exit(status)
  }

  private def run(config: BenchmarkConfig): Unit = {
    val libVersion = OptimizationVersion.resolve(config.libVersion)
    val label = config.label.getOrElse(libVersion)
    val cases = BenchmarkCases.all(config)
    println(s"Benchmarking bb4-optimization $libVersion as '$label': ${cases.size} cells, " +
      s"up to ${config.repetitions} runs each, ${config.maxEvaluations} evaluations, ${config.timeoutSeconds}s limit")

    val results = cases.zipWithIndex.map { case (benchCase, i) =>
      val result = CellRunner.runCase(
        benchCase, config.repetitions, config.timeoutSeconds, config.maxEvaluations, isolated = !config.inProcess)
      println(s"[${i + 1}/${cases.size}] ${benchCase.app} | ${benchCase.algorithm}: ${ReportGenerator.cellText(result, config.timeoutSeconds)}")
      result
    }

    val rt = Runtime.getRuntime
    val record = RunRecord(
      libName = "bb4-optimization",
      libVersion = libVersion,
      label = label,
      timestamp = Instant.now().toString,
      machine = MachineInfo(
        cpus = rt.availableProcessors(),
        os = System.getProperty("os.name"),
        arch = System.getProperty("os.arch"),
        java = System.getProperty("java.version"),
        maxHeapMb = rt.maxMemory() / (1024L * 1024L)
      ),
      timeoutSeconds = config.timeoutSeconds,
      repetitions = config.repetitions,
      maxEvaluations = config.maxEvaluations,
      results = results
    )

    val runs = ResultsStore.upsert(ResultsStore.load(config.resultsFile), record)
    ResultsStore.save(config.resultsFile, runs)
    println(s"Recorded results for '$label' in ${config.resultsFile}")

    if (config.writeReport) {
      writeReport(runs, config.reportFile)
      println(s"Wrote ${config.reportFile}")
    }
  }

  def writeReport(runs: List[RunRecord], reportFile: String): Unit = {
    val path = Paths.get(reportFile)
    val parent = path.toAbsolutePath.getParent
    if (parent != null) Files.createDirectories(parent)
    Files.write(path, ReportGenerator.render(runs).getBytes(StandardCharsets.UTF_8))
  }
}
