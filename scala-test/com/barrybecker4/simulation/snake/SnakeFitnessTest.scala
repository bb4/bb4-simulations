// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.simulation.snake.data.SnakeType
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class SnakeFitnessTest extends AnyFunSuite {

  private val UnstableFitnessPenalty = 100000.0

  test("evaluateFitness with default locomotion params is finite and below unstable penalty") {
    val sim = new SnakeSimulator(SnakeType.TEST_SNAKE.snakeData)
    val params: NumericParameterArray = SnakeSimulator.initialParameters(new Random(1))
    val fitness = sim.evaluateFitness(params)
    assert(!fitness.isNaN && !fitness.isInfinite)
    assert(fitness < UnstableFitnessPenalty)
    assert(fitness > 0.0)
  }

  test("evaluateFitness is deterministic for the same seed and params") {
    val params = SnakeSimulator.initialParameters(new Random(42))
    val sim1 = new SnakeSimulator(SnakeType.TEST_SNAKE.snakeData)
    val sim2 = new SnakeSimulator(SnakeType.TEST_SNAKE.snakeData)
    val f1 = sim1.evaluateFitness(params)
    val f2 = sim2.evaluateFitness(params)
    assert(math.abs(f1 - f2) < 1e-9, s"f1=$f1 f2=$f2")
  }
}
