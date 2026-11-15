// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.data.SnakeType
import org.scalatest.funsuite.AnyFunSuite

class SegmentMuscleTest extends AnyFunSuite {

  private def testSnake(lp: LocomotionParameters = new LocomotionParameters): Snake =
    Snake(SnakeType.TEST_SNAKE.snakeData, lp)

  test("contractMuscles on body segment does not throw for default parameters") {
    val lp = new LocomotionParameters
    val snake = testSnake(lp)
    snake.getSegment(4).contractMuscles(lp, 0.0)
  }

  test("contractMuscles applies opposite left/right contractions") {
    val lp = new LocomotionParameters
    lp.waveAmplitude = 0.1
    lp.direction = 0.0
    val snake = testSnake(lp)
    val seg = snake.getSegment(4)
    val rest0 = seg.edges(0).restingLength
    val rest2 = seg.edges(2).restingLength
    seg.contractMuscles(lp, 0.0)
    val left = seg.edges(0).effectiveLength / rest0
    val right = seg.edges(2).effectiveLength / rest2
    // left = 1+offset, right = 1-offset ⇒ deviations cancel
    assert(math.abs((left - 1.0) + (right - 1.0)) < 1e-9)
    assert(math.abs(left - right) > 1e-6)
  }

  test("contractMuscles throws when amplitude drives a side to non-positive contraction") {
    val lp = new LocomotionParameters
    lp.waveAmplitude = 2.0
    lp.direction = 0.0
    val snake = testSnake(lp)
    var threw = false
    var t = 0.0
    while (t < 50.0 && !threw) {
      try {
        snake.getSegment(4).contractMuscles(lp, t)
      } catch {
        case _: IllegalArgumentException => threw = true
      }
      t += 0.1
    }
    assert(threw, "expected IllegalArgumentException for extreme waveAmplitude")
  }
}
