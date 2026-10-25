// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import java.io.{OutputStream, PrintStream}

/**
  * Runs a single search in its own JVM and prints one result line (see [[WorkerResult]]).
  * Arguments: app, variant, algorithm, seed, maxEvaluations. Started by [[ProcessRunner]].
  */
object BenchmarkWorker {

  def main(args: Array[String]): Unit = {
    val realOut = System.out
    Thread.setDefaultUncaughtExceptionHandler { (_, error) =>
      realOut.println(WorkerResult.formatError(error))
      realOut.flush()
      error.printStackTrace(realOut)
      Runtime.getRuntime.halt(4)
    }
    val status =
      try runWorker(args.toList, realOut)
      catch {
        case t: Throwable =>
          realOut.println(WorkerResult.formatError(t))
          t.printStackTrace(realOut)
          3
      }
    realOut.flush()
    System.exit(status)
  }

  private def runWorker(args: List[String], realOut: PrintStream): Int = args match {
    case app :: variant :: algorithm :: seed :: maxEvaluations :: Nil =>
      val benchCase = BenchmarkCase(app, variant, algorithm)
      val quiet = new PrintStream(OutputStream.nullOutputStream())
      System.setOut(quiet)
      val start = System.nanoTime()
      val fitness = BenchmarkCases.run(benchCase, seed.toLong, maxEvaluations.toInt)
      val millis = (System.nanoTime() - start) / 1000000L
      realOut.println(WorkerResult.format(millis, fitness))
      0
    case _ =>
      realOut.println("Usage: BenchmarkWorker <app> <variant> <algorithm> <seed> <maxEvaluations>")
      2
  }
}
