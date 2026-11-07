// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

object BenchmarkTstUtil {

  def cell(
    algorithm: String,
    app: String = "Snake",
    variant: String = "3 parameters",
    attempts: Int = 3,
    finished: Int = 3,
    timedOut: Int = 0,
    errored: Int = 0,
    medianFitness: Option[Double] = Some(3.14),
    medianMillis: Option[Long] = Some(1200),
    lastError: Option[String] = None
  ): CellResult =
    CellResult(app, variant, algorithm, attempts, finished, timedOut, errored, medianFitness, medianMillis, lastError)

  def runRecord(
    label: String,
    results: List[CellResult],
    libVersion: String = "2.0.0",
    timeoutSeconds: Int = 120,
    repetitions: Int = 3,
    maxEvaluations: Int = 40
  ): RunRecord =
    RunRecord(
      libName = "bb4-optimization",
      libVersion = libVersion,
      label = label,
      timestamp = "2026-10-25T12:00:00Z",
      machine = MachineInfo(cpus = 8, os = "Mac OS X", arch = "aarch64", java = "21", maxHeapMb = 4096),
      timeoutSeconds = timeoutSeconds,
      repetitions = repetitions,
      maxEvaluations = maxEvaluations,
      results = results
    )
}
