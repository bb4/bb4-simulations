// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.data.SnakeType
import com.barrybecker4.simulation.snake.rendering.RenderingParameters
import org.scalatest.funsuite.AnyFunSuite

class SnakeCameraTest extends AnyFunSuite {

  test("effectiveScale grows with the window relative to preferred size") {
    val base = SnakeSimulator.effectiveScale(1.0, SnakeSimulator.PreferredWidth, SnakeSimulator.PreferredHeight)
    val larger = SnakeSimulator.effectiveScale(1.0, SnakeSimulator.PreferredWidth * 2, SnakeSimulator.PreferredHeight * 2)
    assert(math.abs(base - 1.0) < 1e-9)
    assert(math.abs(larger - 2.0) < 1e-9)
  }

  test("cameraTranslation centers world point on the panel without mutating the snake") {
    val updater = new SnakeUpdater()
    val snake = Snake(SnakeType.TEST_SNAKE.snakeData, updater.getLocomotionParams)
    val before = new javax.vecmath.Point2d(snake.getCenter)
    val scale = RenderingParameters.SCALE
    val (tx, ty) = SnakeSimulator.cameraTranslation(960, 800, scale, before.x, before.y)
    // screen position of center: (tx + scale*x, ty + scale*y) == panel center
    assert(math.abs(tx + scale * before.x - 480.0) < 1e-9)
    assert(math.abs(ty + scale * before.y - 400.0) < 1e-9)
    val after = snake.getCenter
    assert(math.abs(after.x - before.x) < 1e-12)
    assert(math.abs(after.y - before.y) < 1e-12)
  }

  test("gridCellCounts covers the panel") {
    val (xDim, yDim) = SnakeSimulator.gridCellCounts(1000, 500, 80.0)
    assert(xDim * 80.0 >= 1000)
    assert(yDim * 80.0 >= 500)
  }
}
