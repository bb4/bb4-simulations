// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.data.SnakeType
import org.scalatest.funsuite.AnyFunSuite

/**
  * Geometry / widths characterization. Does not remap widths — documents current behavior.
  *
  * Intended contract (SnakeData): widths.length == numSegments + 1 cross-sections;
  * segment i should use front widths(i) and rear widths(i+1).
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

  test("characterize rear half-width vs widths(i) for TEST_SNAKE (current behavior)") {
    val data = SnakeType.TEST_SNAKE.snakeData
    val snake = Snake(data, new LocomotionParameters)
    val actualRears = (0 until data.numSegments).map(i => rearHalfWidth(snake, i))

    // Head rear uses widths(1) — matches intended segment-0 rear.
    assert(
      math.abs(actualRears(0) - data.widths(1)) < 1e-6,
      s"head rear=${actualRears(0)}, widths(1)=${data.widths(1)}"
    )

    // Intended: segment i rear == widths(i+1).
    val intendedRears = (0 until data.numSegments).map(i => data.widths(i + 1))
    val matchesIntended =
      actualRears.zip(intendedRears).forall { case (a, e) => math.abs(a - e) < 1e-6 }

    // Document: last width entry is unused by current construction.
    val lastWidth = data.widths(data.numSegments)
    val usesLastWidth = actualRears.exists(w => math.abs(w - lastWidth) < 1e-6)
    assert(
      !usesLastWidth || matchesIntended,
      "If last width is used, geometry should match intended mapping"
    )

    // Body segment 1 rear currently equals widths(1) (same as head rear), not widths(2).
    assert(
      math.abs(actualRears(1) - data.widths(1)) < 1e-6,
      s"current body[1] rear=${actualRears(1)}; expected current behavior widths(1)=${data.widths(1)}"
    )
    assert(
      math.abs(actualRears(1) - data.widths(2)) > 1e-6,
      "characterization: body[1] rear currently differs from intended widths(2)"
    )

    assert(!matchesIntended, "if this fails, width indexing already matches intended — update design")
  }
}
