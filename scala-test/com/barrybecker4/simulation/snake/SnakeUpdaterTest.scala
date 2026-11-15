// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.data.SnakeType
import org.scalatest.funsuite.AnyFunSuite

class SnakeUpdaterTest extends AnyFunSuite {

  /** Production wiring: Snake and SnakeUpdater share one LocomotionParameters instance. */
  private def sharedSnakeAndUpdater(): (Snake, SnakeUpdater) = {
    val updater = new SnakeUpdater()
    val snake = Snake(SnakeType.TEST_SNAKE.snakeData, updater.getLocomotionParams)
    (snake, updater)
  }

  test("stepForward smoke: no NaN in particle positions with shared locomotion params") {
    val (snake, updater) = sharedSnakeAndUpdater()
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

  test("massScale change on shared params is applied to particles on stepForward") {
    val (snake, updater) = sharedSnakeAndUpdater()
    val params = updater.getLocomotionParams
    val before = snake.getSegment(0).getCenterParticle.mass
    params.massScale = params.massScale * 2.0
    updater.stepForward(snake, 0.2)
    val after = snake.getSegment(0).getCenterParticle.mass
    assert(math.abs(after - before * 2.0) < 1e-9, s"before=$before after=$after")
  }

  test("fresh TEST_SNAKE is stable") {
    val (snake, _) = sharedSnakeAndUpdater()
    assert(snake.isStable)
  }

  test("kinked mid-body edges make snake unstable") {
    val (snake, _) = sharedSnakeAndUpdater()
    assert(snake.isStable)
    val seg = snake.getSegment(3)
    val c = seg.getCenterParticle
    // Bend side edges relative to the segment in front (isStable uses edge direction dots)
    seg.particles(0).set(c.x - 5, c.y + 40)
    seg.particles(3).set(c.x - 5, c.y - 40)
    assert(!snake.isStable)
  }

  test("very large timestep returns a reduced step when integration is unstable") {
    val (snake, updater) = sharedSnakeAndUpdater()
    updater.getLocomotionParams.springK = 4.0
    updater.getLocomotionParams.massScale = 0.1
    var dt = 0.2
    var i = 0
    while (i < 5) {
      dt = updater.stepForward(snake, dt)
      i += 1
    }
    val huge = 50.0
    val next = updater.stepForward(snake, huge)
    assert(next <= huge)
    assert(!next.isNaN)
  }
}
