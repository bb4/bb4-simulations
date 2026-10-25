// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

/**
  * Settings for a benchmark run. Every setting has a default and can be overridden with a `--name=value` argument.
  *
  * @param label     column name in the report. Empty means "use the detected library version".
  * @param algorithms empty means every strategy in [[BenchmarkCases.Strategies]]
  */
final case class BenchmarkConfig(
  timeoutSeconds: Int = BenchmarkConfig.DEFAULT_TIMEOUT_SECONDS,
  repetitions: Int = BenchmarkConfig.DEFAULT_REPETITIONS,
  maxEvaluations: Int = BenchmarkConfig.DEFAULT_MAX_EVALUATIONS,
  apps: Set[String] = BenchmarkConfig.ALL_APPS,
  algorithms: Set[String] = Set.empty,
  libVersion: Option[String] = None,
  label: Option[String] = None,
  resultsFile: String = BenchmarkConfig.DEFAULT_RESULTS_FILE,
  reportFile: String = BenchmarkConfig.DEFAULT_REPORT_FILE,
  writeReport: Boolean = true,
  inProcess: Boolean = false
)

object BenchmarkConfig {

  val DEFAULT_TIMEOUT_SECONDS = 120
  val DEFAULT_REPETITIONS = 3
  val DEFAULT_MAX_EVALUATIONS = 40
  val ALL_APPS: Set[String] = Set("snake", "trebuchet")
  val DEFAULT_RESULTS_FILE = "docs/benchmarks.json"
  val DEFAULT_REPORT_FILE = "docs/benchmarks.md"

  val USAGE: String =
    """Options (all optional):
      |  --lib-version=2.0.0     the bb4-optimization version you expect; the run fails if the classpath disagrees
      |  --label=2.0.0           column name; defaults to the detected library version
      |  --timeout=120           seconds before a run is killed
      |  --reps=3                runs per cell; the median of the finished runs is reported
      |  --max-evals=40          evaluation budget for each run
      |  --apps=snake,trebuchet  which simulations to run
      |  --algorithms=GENETIC_SEARCH,HILL_CLIMBING   subset of strategies; default is all of them
      |  --results=docs/benchmarks.json
      |  --report=docs/benchmarks.md
      |  --no-report             do not write the markdown report
      |  --in-process            run searches in this JVM instead of one JVM per run (a runaway search cannot be killed)""".stripMargin

  /** @throws IllegalArgumentException for an unknown or malformed option */
  def parse(args: List[String]): BenchmarkConfig =
    args.foldLeft(BenchmarkConfig())((config, arg) => applyArg(config, arg))

  private def positiveInt(name: String, value: String): Int = {
    val n =
      try value.trim.toInt
      catch {
        case _: NumberFormatException => throw new IllegalArgumentException(s"--$name must be a whole number, got '$value'")
      }
    if (n < 1) throw new IllegalArgumentException(s"--$name must be at least 1, got $n")
    n
  }

  private def applyArg(config: BenchmarkConfig, arg: String): BenchmarkConfig = {
    if (arg == "--no-report") config.copy(writeReport = false)
    else if (arg == "--in-process") config.copy(inProcess = true)
    else if (arg.startsWith("--") && arg.contains("=")) {
      val name = arg.substring(2, arg.indexOf('='))
      val value = arg.substring(arg.indexOf('=') + 1)
      name match {
        case "lib-version" => config.copy(libVersion = Some(value.trim))
        case "label" => config.copy(label = Some(value.trim))
        case "timeout" => config.copy(timeoutSeconds = positiveInt(name, value))
        case "reps" => config.copy(repetitions = positiveInt(name, value))
        case "max-evals" => config.copy(maxEvaluations = positiveInt(name, value))
        case "apps" =>
          val chosen = value.split(',').toList.map(_.trim.toLowerCase).filter(_.nonEmpty).toSet
          val unknown = chosen.diff(ALL_APPS)
          if (unknown.nonEmpty)
            throw new IllegalArgumentException(s"Unknown app(s): ${unknown.mkString(", ")}. Choose from ${ALL_APPS.mkString(", ")}")
          config.copy(apps = chosen)
        case "algorithms" =>
          val chosen = value.split(',').toList.map(_.trim).filter(_.nonEmpty).toSet
          val unknown = chosen.diff(BenchmarkCases.Strategies.toSet)
          if (unknown.nonEmpty)
            throw new IllegalArgumentException(
              s"Unknown algorithm(s): ${unknown.mkString(", ")}. Choose from ${BenchmarkCases.Strategies.mkString(", ")}")
          config.copy(algorithms = chosen)
        case "results" => config.copy(resultsFile = value)
        case "report" => config.copy(reportFile = value)
        case _ => throw new IllegalArgumentException(s"Unknown option --$name\n$USAGE")
      }
    } else throw new IllegalArgumentException(s"Unrecognized argument '$arg'\n$USAGE")
  }
}
