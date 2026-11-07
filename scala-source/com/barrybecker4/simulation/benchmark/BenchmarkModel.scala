// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

/**
  * Aggregated result of repeatedly running one strategy on one simulation.
  *
  * @param finished      runs that returned a fitness within the time limit
  * @param medianFitness median fitness of the finished runs. Lower is better.
  * @param medianMillis  median wall clock time of the finished runs
  */
final case class CellResult(
  app: String,
  variant: String,
  algorithm: String,
  attempts: Int,
  finished: Int,
  timedOut: Int,
  errored: Int,
  medianFitness: Option[Double],
  medianMillis: Option[Long],
  lastError: Option[String] = None
)

/** Description of the machine a benchmark ran on. Timings are only comparable between runs on the same machine. */
final case class MachineInfo(cpus: Int, os: String, arch: String, java: String, maxHeapMb: Long)

/**
  * Everything recorded by one execution of the benchmark.
  *
  * @param libVersion the bb4-optimization version detected on the classpath, like "2.0.0"
  * @param label      the column name. Defaults to [[libVersion]]. A later run with different options
  *                   can use another label, such as "2.1-SNAPSHOT+options", without a second jar.
  */
final case class RunRecord(
  libName: String,
  libVersion: String,
  label: String,
  timestamp: String,
  machine: MachineInfo,
  timeoutSeconds: Int,
  repetitions: Int,
  maxEvaluations: Int,
  results: List[CellResult]
)
