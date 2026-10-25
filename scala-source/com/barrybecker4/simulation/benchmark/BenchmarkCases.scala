// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import com.barrybecker4.math.MathUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.optimizee.{EvaluationBudgetExceededException, Optimizee}
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.optimization.strategy.OptimizationStrategyType
import com.barrybecker4.simulation.snake.SnakeSimulator
import com.barrybecker4.simulation.trebuchet.{TrebuchetParameters, TrebuchetSimulator}

import scala.util.Random

/** One strategy on one simulation. */
final case class BenchmarkCase(app: String, variant: String, algorithm: String)

/**
  * Snake and trebuchet, each against the continuous strategies that exist in bb4-optimization 2.0.0.
  * The benchmark calls [[Optimizer]] on a paused simulator. It does not call `doOptimization`, which would
  * unpause the animation and, for the trebuchet, also apply the result to the window.
  */
object BenchmarkCases {

  val SNAKE = "Snake"
  val TREBUCHET = "Trebuchet"

  /** The fitness scale both apps pass to the optimizer today. */
  val FitnessRange = 0.3

  /** Strategies that can search a bounded continuous parameter array in 2.0.0. */
  val Strategies: List[String] = List(
    "GLOBAL_SAMPLING",
    "GLOBAL_HILL_CLIMBING",
    "HILL_CLIMBING",
    "SIMULATED_ANNEALING",
    "GENETIC_SEARCH",
    "CONCURRENT_GENETIC_SEARCH"
  )

  def all(config: BenchmarkConfig): List[BenchmarkCase] = {
    val algorithms = if (config.algorithms.isEmpty) Strategies else Strategies.filter(config.algorithms.contains)
    val apps =
      (if (config.apps.contains("snake")) List(BenchmarkCase(SNAKE, "3 parameters", "")) else Nil) ++
        (if (config.apps.contains("trebuchet")) List(BenchmarkCase(TREBUCHET, "6 parameters", "")) else Nil)
    for {
      template <- apps
      algorithm <- algorithms
    } yield template.copy(algorithm = algorithm)
  }

  /**
    * Run one search and return the best fitness. Lower is better.
    * bb4-optimization 2.0.0 throws [[EvaluationBudgetExceededException]] when the evaluation budget is used up
    * instead of returning the best parameters, so the best fitness seen is kept here.
    */
  def run(benchCase: BenchmarkCase, seed: Long, maxEvaluations: Int): Double = {
    MathUtil.RANDOM.setSeed(seed)
    val strategy = OptimizationStrategyType.valueOf(benchCase.algorithm)
    val rnd = new Random(seed)
    benchCase.app match {
      case SNAKE =>
        val simulator = new SnakeSimulator()
        simulator.setPaused(true)
        search(simulator, SnakeSimulator.initialParameters(new Random(seed)), strategy, rnd, maxEvaluations)
      case TREBUCHET =>
        val simulator = new TrebuchetSimulator()
        simulator.setPaused(true)
        val params = TrebuchetParameters.initialArray(simulator.getTrebuchet, new Random(seed))
        search(simulator, params, strategy, rnd, maxEvaluations)
      case other =>
        throw new IllegalArgumentException(s"Unknown simulation '$other'")
    }
  }

  private def search(
    optimizee: Optimizee,
    params: ParameterArray,
    strategy: OptimizationStrategyType,
    rnd: Random,
    maxEvaluations: Int
  ): Double = {
    val tracked = new BestFitness(optimizee)
    val returned =
      try Some(new Optimizer(tracked).doOptimization(strategy, params, FitnessRange, rnd, Some(maxEvaluations)).fitness)
      catch { case _: EvaluationBudgetExceededException => None }
    (returned.toList ++ tracked.bestFitness.toList) match {
      case Nil => throw new IllegalStateException(s"${strategy.toString} returned no fitness")
      case scores => scores.min
    }
  }

  /** Remembers the lowest fitness passed to [[evaluateFitness]]. */
  private final class BestFitness(delegate: Optimizee) extends Optimizee {
    var bestFitness: Option[Double] = None

    override def getName: String = delegate.getName
    override def evaluateByComparison: Boolean = delegate.evaluateByComparison
    override def getOptimalFitness: Double = delegate.getOptimalFitness
    override def compareFitness(params1: ParameterArray, params2: ParameterArray): Double =
      delegate.compareFitness(params1, params2)

    override def evaluateFitness(params: ParameterArray): Double = {
      val fitness = delegate.evaluateFitness(params)
      bestFitness = Some(bestFitness.fold(fitness)(math.min(_, fitness)))
      fitness
    }
  }
}
