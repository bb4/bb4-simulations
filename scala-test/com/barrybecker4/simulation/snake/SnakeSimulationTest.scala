// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.data.SnakeType
import org.scalatest.funsuite.AnyFunSuite

/** Smoke entry point; detailed cases live in focused suites. */
class SnakeSimulationTest extends AnyFunSuite {

  test("shared SnakeUpdater wiring constructs a runnable TEST_SNAKE") {
    val updater = new SnakeUpdater()
    val snake = Snake(SnakeType.TEST_SNAKE.snakeData, updater.getLocomotionParams)
    assert(snake.getNumSegments == 22)
    assert(snake.getSegment(0).isHead)
    val dt = updater.stepForward(snake, 0.2)
    assert(dt > 0.0 && !dt.isNaN)
  }
}
