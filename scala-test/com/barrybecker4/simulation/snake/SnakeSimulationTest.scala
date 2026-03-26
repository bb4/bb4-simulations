// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.data.SnakeType
import org.scalatest.funsuite.AnyFunSuite

class SnakeSimulationTest extends AnyFunSuite {

  test("TEST_SNAKE invariants after construction") {
    val lp = new LocomotionParameters
    val snake = Snake(SnakeType.TEST_SNAKE.snakeData, lp)
    assert(snake.getNumSegments == 22)
    assert(snake.getSegment(0).isHead)
    val c = snake.getCenter
    assert(!c.x.isNaN && !c.y.isNaN)
  }

  test("contractMuscles on body segment does not throw for default parameters") {
    val lp = new LocomotionParameters
    val snake = Snake(SnakeType.TEST_SNAKE.snakeData, lp)
    snake.getSegment(4).contractMuscles(lp, 0.0)
  }

  test("stepForward smoke: no NaN in particle positions") {
    val lp = new LocomotionParameters
    val snake = Snake(SnakeType.TEST_SNAKE.snakeData, lp)
    val updater = new SnakeUpdater()
    var dt = 0.2
    var i = 0
    while (i < 50) {
      dt = updater.stepForward(snake, dt)
      i += 1
    }
    for (si <- 0 until snake.getNumSegments) {
      val seg = snake.getSegment(si)
      for (pj <- 0 until 5) {
        val p = seg.particles(pj)
        if (p != null) {
          assert(!p.x.isNaN && !p.y.isNaN)
        }
      }
    }
  }
}
