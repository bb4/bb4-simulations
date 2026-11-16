// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.gait

import com.barrybecker4.simulation.snake.data.SnakeType
import com.barrybecker4.simulation.snake.{Snake, SnakeSimulator, SnakeUpdater}
import org.scalatest.funsuite.AnyFunSuite

class GaitStabilityTest extends AnyFunSuite {

  private def maxEdgeStretch(snake: Snake): Double = {
    var max = 0.0
    var si = 0
    while (si < snake.getNumSegments) {
      val edges = snake.getSegment(si).edges
      var ei = 0
      while (ei < edges.length) {
        val e = edges(ei)
        if (e != null && e.restingLength > 0) {
          val r = e.length / e.restingLength
          if (r > max) max = r
        }
        ei += 1
      }
      si += 1
    }
    max
  }

  private def assertStableRun(gait: GaitType, amp: Double, steps: Int = 3000): Unit = {
    val updater = new SnakeUpdater()
    val lp = updater.getLocomotionParams
    lp.gaitType = gait
    lp.waveAmplitude = amp
    val snake = Snake(SnakeType.LONG_SNAKE.snakeData, lp)
    var dt = SnakeSimulator.INITIAL_TIME_STEP
    var i = 0
    while (i < steps) {
      dt = updater.stepForward(snake, dt)
      val c = snake.getCenter
      assert(!c.x.isNaN && !c.y.isNaN, s"$gait produced NaN center at step $i")
      assert(snake.isStable, s"$gait became unstable at step $i")
      i += 1
    }
    assert(maxEdgeStretch(snake) < 2.0, s"$gait edge stretch too large: ${maxEdgeStretch(snake)}")
  }

  test("all gaits remain stable at default amplitude") {
    for (gait <- GaitType.VALUES) {
      assertStableRun(gait, amp = 0.027)
    }
  }

  test("rectilinear and concertina remain stable at high UI amplitude") {
    assertStableRun(GaitType.RECTILINEAR, amp = 0.20)
    assertStableRun(GaitType.CONCERTINA, amp = 0.20)
  }
}
