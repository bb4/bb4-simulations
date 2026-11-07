// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import com.barrybecker4.simulation.benchmark.Json.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

/**
  * Reads and writes docs/benchmarks.json. The file holds one [[RunRecord]] per column label, so adding a
  * library version never disturbs the numbers already recorded for an older one.
  */
object ResultsStore {

  val SCHEMA_VERSION = 1

  def load(file: String): List[RunRecord] = {
    val path = Paths.get(file)
    if (!Files.exists(path)) Nil
    else fromJson(Json.parse(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)))
  }

  def save(file: String, runs: List[RunRecord]): Unit = {
    val path: Path = Paths.get(file)
    val parent = path.toAbsolutePath.getParent
    if (parent != null) Files.createDirectories(parent)
    Files.write(path, Json.write(toJson(runs)).getBytes(StandardCharsets.UTF_8))
  }

  /** Replace the run with the same label if there is one, otherwise append. */
  def upsert(runs: List[RunRecord], run: RunRecord): List[RunRecord] =
    if (runs.exists(_.label == run.label)) runs.map(r => if (r.label == run.label) run else r)
    else runs :+ run

  def toJson(runs: List[RunRecord]): Json =
    JObj(List(
      "schemaVersion" -> JNum(SCHEMA_VERSION),
      "runs" -> JArr(runs.map(runToJson))
    ))

  def fromJson(json: Json): List[RunRecord] = json match {
    case obj: JObj =>
      obj.get("runs") match {
        case Some(JArr(items)) => items.map(runFromJson)
        case _ => throw new IllegalArgumentException("Benchmark results file has no 'runs' array")
      }
    case _ => throw new IllegalArgumentException("Benchmark results file must contain a JSON object")
  }

  private def optNum(value: Option[Double]): Json = value.map(JNum(_)).getOrElse(JNull)

  private def runToJson(run: RunRecord): Json =
    JObj(List(
      "libName" -> JStr(run.libName),
      "libVersion" -> JStr(run.libVersion),
      "label" -> JStr(run.label),
      "timestamp" -> JStr(run.timestamp),
      "machine" -> JObj(List(
        "cpus" -> JNum(run.machine.cpus),
        "os" -> JStr(run.machine.os),
        "arch" -> JStr(run.machine.arch),
        "java" -> JStr(run.machine.java),
        "maxHeapMb" -> JNum(run.machine.maxHeapMb.toDouble)
      )),
      "timeoutSeconds" -> JNum(run.timeoutSeconds),
      "repetitions" -> JNum(run.repetitions),
      "maxEvaluations" -> JNum(run.maxEvaluations),
      "results" -> JArr(run.results.map(cellToJson))
    ))

  private def cellToJson(c: CellResult): Json =
    JObj(List(
      "app" -> JStr(c.app),
      "variant" -> JStr(c.variant),
      "algorithm" -> JStr(c.algorithm),
      "attempts" -> JNum(c.attempts),
      "finished" -> JNum(c.finished),
      "timedOut" -> JNum(c.timedOut),
      "errored" -> JNum(c.errored),
      "medianFitness" -> optNum(c.medianFitness),
      "medianMillis" -> optNum(c.medianMillis.map(_.toDouble)),
      "lastError" -> c.lastError.map(JStr(_)).getOrElse(JNull)
    ))

  private def field(obj: JObj, name: String): Json =
    obj.get(name).getOrElse(throw new IllegalArgumentException(s"Missing field '$name' in benchmark results"))

  private def str(obj: JObj, name: String): String = field(obj, name) match {
    case JStr(s) => s
    case _ => throw new IllegalArgumentException(s"Field '$name' must be a string")
  }

  private def long(obj: JObj, name: String): Long = field(obj, name) match {
    case JNum(n) => n.toLong
    case _ => throw new IllegalArgumentException(s"Field '$name' must be a number")
  }

  private def optDouble(obj: JObj, name: String): Option[Double] = obj.get(name) match {
    case Some(JNum(n)) => Some(n)
    case _ => None
  }

  private def optStr(obj: JObj, name: String): Option[String] = obj.get(name) match {
    case Some(JStr(s)) => Some(s)
    case _ => None
  }

  private def objField(obj: JObj, name: String): JObj = field(obj, name) match {
    case o: JObj => o
    case _ => throw new IllegalArgumentException(s"Field '$name' must be an object")
  }

  private def runFromJson(json: Json): RunRecord = json match {
    case obj: JObj =>
      val machine = objField(obj, "machine")
      val results = field(obj, "results") match {
        case JArr(items) => items.map(cellFromJson)
        case _ => throw new IllegalArgumentException("Field 'results' must be an array")
      }
      val libVersion = str(obj, "libVersion")
      RunRecord(
        libName = str(obj, "libName"),
        libVersion = libVersion,
        label = optStr(obj, "label").getOrElse(libVersion),
        timestamp = str(obj, "timestamp"),
        machine = MachineInfo(
          cpus = long(machine, "cpus").toInt,
          os = str(machine, "os"),
          arch = str(machine, "arch"),
          java = str(machine, "java"),
          maxHeapMb = long(machine, "maxHeapMb")
        ),
        timeoutSeconds = long(obj, "timeoutSeconds").toInt,
        repetitions = long(obj, "repetitions").toInt,
        maxEvaluations = obj.get("maxEvaluations").collect { case JNum(n) => n.toInt }.getOrElse(BenchmarkConfig.DEFAULT_MAX_EVALUATIONS),
        results = results
      )
    case _ => throw new IllegalArgumentException("Each run must be a JSON object")
  }

  private def cellFromJson(json: Json): CellResult = json match {
    case obj: JObj =>
      CellResult(
        app = str(obj, "app"),
        variant = str(obj, "variant"),
        algorithm = str(obj, "algorithm"),
        attempts = long(obj, "attempts").toInt,
        finished = long(obj, "finished").toInt,
        timedOut = long(obj, "timedOut").toInt,
        errored = long(obj, "errored").toInt,
        medianFitness = optDouble(obj, "medianFitness"),
        medianMillis = optDouble(obj, "medianMillis").map(_.toLong),
        lastError = optStr(obj, "lastError")
      )
    case _ => throw new IllegalArgumentException("Each result must be a JSON object")
  }
}
