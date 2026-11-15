// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.data.SnakeType
import org.scalatest.funsuite.AnyFunSuite

/**
  * Geometry / widths: segment i rear cross-section uses widths(i+1)
  * (SnakeData: widths.length == numSegments + 1).
  */
class SnakeGeometryTest extends AnyFunSuite {

  private def rearHalfWidth(snake: Snake, segmentIndex: Int): Double = {
    val particles = snake.getSegment(segmentIndex).particles
    particles(0).distance(particles(3))
  }

  test("TEST_SNAKE invariants after construction") {
    val lp = new LocomotionParameters
    val snake = Snake(SnakeType.TEST_SNAKE.snakeData, lp)
    assert(snake.getNumSegments == 22)
    assert(snake.getSegment(0).isHead)
    assert(snake.getSegment(snake.getNumSegments - 1).isTail)
    val c = snake.getCenter
    assert(!c.x.isNaN && !c.y.isNaN)
  }

  test("SnakeData widths length is numSegments + 1 for all SnakeType presets") {
    SnakeType.values.foreach { st =>
      val data = st.snakeData
      assert(
        data.widths.length == data.numSegments + 1,
        s"${st.name}: widths.length=${data.widths.length}, expected ${data.numSegments + 1}"
      )
    }
  }

  test("each segment rear half-width matches widths(i+1) for TEST_SNAKE") {
    val data = SnakeType.TEST_SNAKE.snakeData
    val snake = Snake(data, new LocomotionParameters)
    for (i <- 0 until data.numSegments) {
      val actual = rearHalfWidth(snake, i)
      val expected = data.widths(i + 1)
      assert(
        math.abs(actual - expected) < 1e-6,
        s"segment $i rear=$actual, expected widths(${i + 1})=$expected"
      )
    }
  }
}
