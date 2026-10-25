// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import com.barrybecker4.math.MathUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.optimizee.{EvaluationBudgetExceededException, Optimizee}
import com.barrybecker4.optimization.parameter.{NumericParameterArray, ParameterArray}
import com.barrybecker4.optimization.parameter.types.DoubleParameter
import com.barrybecker4.optimization.strategy.OptimizationStrategyType
import com.barrybecker4.simulation.snake.SnakeSimulator
import com.barrybecker4.simulation.trebuchet.{TrebuchetParameters, TrebuchetSimulator}

import scala.util.Random

/** One strategy on one simulation. */
final case class BenchmarkCase(app: String, variant: String, algorithm: String)

/**
  * Snake and trebuchet, each against every strategy for which `appliesTo` is true on a bounded continuous problem.
  * The benchmark calls [[Optimizer]] on a paused simulator. It does not call `doOptimization`, which would
  * unpause the animation and, for the trebuchet, also apply the result to the window.
  */
object BenchmarkCases {

  val SNAKE = "Snake"
  val TREBUCHET = "Trebuchet"

  /** The fitness scale both apps pass to the optimizer today. */
  val FitnessRange = 0.3

  /**
    * Strategies that can search a bounded continuous parameter array.
    * Kept in enum order so a new strategy shows up when `appliesTo` is true for this kind of problem.
    */
  val Strategies: List[String] = {
    val params = NumericParameterArray(IndexedSeq(DoubleParameter(0.5, 0.0, 1.0, "x")), 5, new Random(1))
    OptimizationStrategyType.values.toList.filter(_.appliesTo(ContinuousProblem, params)).map(_.toString)
  }

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
    * `doOptimization` returns [[com.barrybecker4.optimization.OptimizationResult]] and, when the evaluation budget
    * is used up, best-so-far with status BudgetExhausted. The best fitness seen is kept as well, in case the
    * returned value is missing.
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
      try Some(new Optimizer(tracked).doOptimization(strategy, params, FitnessRange, rnd, Some(maxEvaluations)).best.fitness)
      catch { case _: EvaluationBudgetExceededException => None }
    (returned.toList ++ tracked.bestFitness.toList) match {
      case Nil => throw new IllegalStateException(s"${strategy.toString} returned no fitness")
      case scores => scores.min
    }
  }

  /** A bounded continuous problem: not a comparison, and not a discrete state space. */
  private object ContinuousProblem extends Optimizee {
    override def getName: String = "continuous"
    override def evaluateByComparison: Boolean = false
    override def evaluateFitness(params: ParameterArray): Double = 0
    override def compareFitness(params1: ParameterArray, params2: ParameterArray): Double = 0
    override def getOptimalFitness: Double = 0
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
