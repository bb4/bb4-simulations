// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.lang.management.ManagementFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ConcurrentLinkedDeque, TimeUnit}
import scala.jdk.CollectionConverters.*

/** One execution of one strategy. */
sealed trait RunOutcome
final case class Finished(millis: Long, fitness: Double) extends RunOutcome
final case class TimedOut() extends RunOutcome
final case class Errored(message: String) extends RunOutcome

/** The lines a [[BenchmarkWorker]] prints so the parent can tell a finished search from a crash. */
object WorkerResult {

  val PREFIX = "BENCH_RESULT "
  val ERROR_PREFIX = "BENCH_ERROR "

  def formatError(error: Throwable): String = {
    val firstLine = Option(error.getMessage).getOrElse("").linesIterator.nextOption().getOrElse("")
    s"$ERROR_PREFIX${error.getClass.getName}${if (firstLine.isEmpty) "" else ": " + firstLine}"
  }

  def parseError(line: String): Option[String] =
    if (line.startsWith(ERROR_PREFIX)) Some(line.substring(ERROR_PREFIX.length).trim).filter(_.nonEmpty) else None

  def format(millis: Long, fitness: Double): String =
    s"$PREFIX${millis} ${java.lang.Double.toString(fitness)}"

  def parse(line: String): Option[Finished] =
    if (!line.startsWith(PREFIX)) None
    else
      line.substring(PREFIX.length).trim.split("\\s+").toList match {
        case millis :: fitness :: Nil =>
          try Some(Finished(millis.toLong, fitness.toDouble))
          catch { case _: NumberFormatException => None }
        case _ => None
      }
}

/**
  * Runs each search in a separate JVM and kills the process if it exceeds the time limit.
  * A stuck physics step cannot be stopped by interrupting a thread.
  */
object ProcessRunner {

  private val WORKER_CLASS = "com.barrybecker4.simulation.benchmark.BenchmarkWorker"
  private val STARTUP_ALLOWANCE_MILLIS = 20000L
  private val KILL_GRACE_SECONDS = 10L
  private val TAIL_LINES = 8

  def runOnce(benchCase: BenchmarkCase, seed: Long, timeoutMillis: Long, maxEvaluations: Int): RunOutcome = {
    val process = new ProcessBuilder(command(benchCase, seed, maxEvaluations).asJava).redirectErrorStream(true).start()
    val resultLine = new AtomicReference[String](null)
    val errorLine = new AtomicReference[String](null)
    val tail = new ConcurrentLinkedDeque[String]()

    val reader = new Thread(() => {
      val in = new BufferedReader(new InputStreamReader(process.getInputStream, StandardCharsets.UTF_8))
      try {
        var line = in.readLine()
        while (line != null) {
          if (line.startsWith(WorkerResult.PREFIX)) resultLine.set(line)
          else if (line.startsWith(WorkerResult.ERROR_PREFIX)) errorLine.compareAndSet(null, line)
          else {
            tail.addLast(line)
            while (tail.size > TAIL_LINES) tail.pollFirst()
          }
          line = in.readLine()
        }
      } catch {
        case _: IOException => ()
      } finally in.close()
    }, "benchmark-worker-output")
    reader.setDaemon(true)
    reader.start()

    if (process.waitFor(timeoutMillis + STARTUP_ALLOWANCE_MILLIS, TimeUnit.MILLISECONDS)) {
      reader.join(5000L)
      interpret(resultLine.get(), errorLine.get(), process.exitValue(), tail.asScala.toList, timeoutMillis)
    } else {
      process.destroyForcibly()
      process.waitFor(KILL_GRACE_SECONDS, TimeUnit.SECONDS)
      reader.join(2000L)
      TimedOut()
    }
  }

  def interpret(resultLine: String, errorLine: String, exitCode: Int, tail: List[String], timeoutMillis: Long): RunOutcome =
    Option(resultLine).flatMap(WorkerResult.parse) match {
      case Some(Finished(millis, _)) if millis > timeoutMillis => TimedOut()
      case Some(finished) => finished
      case None =>
        Errored(Option(errorLine).flatMap(WorkerResult.parseError).getOrElse {
          val details = if (tail.isEmpty) "" else ": " + tail.mkString(" | ")
          s"worker exited with code $exitCode without reporting a result$details"
        })
    }

  private def command(benchCase: BenchmarkCase, seed: Long, maxEvaluations: Int): List[String] = {
    val java = Paths.get(System.getProperty("java.home"), "bin", "java").toString
    val jvmArgs = ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList
      .filterNot(arg => arg.startsWith("-agentlib:jdwp") || arg.startsWith("-javaagent"))
    List(java) ++ jvmArgs ++ List(
      "-cp", System.getProperty("java.class.path"),
      WORKER_CLASS,
      benchCase.app, benchCase.variant, benchCase.algorithm, seed.toString, maxEvaluations.toString
    )
  }
}
