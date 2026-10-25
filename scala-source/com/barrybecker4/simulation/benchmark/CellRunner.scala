// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import scala.collection.mutable.ListBuffer

/** Runs the repetitions of one cell and summarizes them. */
object CellRunner {

  /** After this many timeouts or errors in a row the remaining repetitions are skipped. */
  val MAX_CONSECUTIVE_FAILURES = 2

  def runCase(
    benchCase: BenchmarkCase,
    repetitions: Int,
    timeoutSeconds: Int,
    maxEvaluations: Int,
    isolated: Boolean
  ): CellResult = {
    val outcomes = ListBuffer[RunOutcome]()
    var consecutiveFailures = 0
    var rep = 0
    while (rep < repetitions && consecutiveFailures < MAX_CONSECUTIVE_FAILURES) {
      val seed = rep + 1L
      val outcome =
        if (isolated) ProcessRunner.runOnce(benchCase, seed, timeoutSeconds * 1000L, maxEvaluations)
        else runInProcess(benchCase, seed, timeoutSeconds * 1000L, maxEvaluations)
      outcomes += outcome
      outcome match {
        case _: TimedOut | _: Errored => consecutiveFailures += 1
        case _: Finished => consecutiveFailures = 0
      }
      rep += 1
    }
    CellStats.aggregate(benchCase, outcomes.toList)
  }

  /** Times a search in this JVM. A search that ignores interruption cannot be stopped. */
  def runInProcess(benchCase: BenchmarkCase, seed: Long, timeoutMillis: Long, maxEvaluations: Int): RunOutcome = {
    val outcome = new java.util.concurrent.atomic.AtomicReference[RunOutcome](Errored("The search did not report a result"))
    val worker = new Thread(() => {
      try {
        val start = System.nanoTime()
        val fitness = BenchmarkCases.run(benchCase, seed, maxEvaluations)
        val millis = (System.nanoTime() - start) / 1000000L
        outcome.set(Finished(millis, fitness))
      } catch {
        case t: Throwable => outcome.set(Errored(t.toString))
      }
    }, "benchmark-search")
    worker.setDaemon(true)
    worker.start()
    worker.join(timeoutMillis)
    if (worker.isAlive) {
      worker.interrupt()
      TimedOut()
    } else outcome.get()
  }
}

object CellStats {

  def aggregate(benchCase: BenchmarkCase, outcomes: List[RunOutcome]): CellResult = {
    val finished = outcomes.collect { case f: Finished => f }
    CellResult(
      app = benchCase.app,
      variant = benchCase.variant,
      algorithm = benchCase.algorithm,
      attempts = outcomes.size,
      finished = finished.size,
      timedOut = outcomes.count(_.isInstanceOf[TimedOut]),
      errored = outcomes.count(_.isInstanceOf[Errored]),
      medianFitness = median(finished.map(_.fitness)),
      medianMillis = medianLong(finished.map(_.millis)),
      lastError = outcomes.collect { case Errored(message) => message }.lastOption
    )
  }

  def median(values: List[Double]): Option[Double] =
    if (values.isEmpty) None
    else {
      val sorted = values.sorted
      val n = sorted.size
      Some(if (n % 2 == 1) sorted(n / 2) else (sorted(n / 2 - 1) + sorted(n / 2)) / 2.0)
    }

  def medianLong(values: List[Long]): Option[Long] =
    if (values.isEmpty) None
    else {
      val sorted = values.sorted
      val n = sorted.size
      Some(if (n % 2 == 1) sorted(n / 2) else (sorted(n / 2 - 1) + sorted(n / 2)) / 2)
    }
}
