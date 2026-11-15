package com.barrybecker4.simulation.water.ui

import com.barrybecker4.simulation.water.model.Environment
import org.scalatest.funsuite.AnyFunSuite

class InteractionHandlerSuite extends AnyFunSuite {

  test("columnAt clamps pixel x to valid column indices") {
    assert(InteractionHandler.columnAt(0, 1, 10, 0) == 0)
    assert(InteractionHandler.columnAt(5, 1, 10, 0) == 5)
    assert(InteractionHandler.columnAt(100, 1, 10, 0) == 9)
    assert(InteractionHandler.columnAt(-3, 1, 10, 0) == 0)
    assert(InteractionHandler.columnAt(20, 4, 10, 0) == 5)
  }

  test("columnAt subtracts offset before dividing by xStep") {
    val offset = 10
    assert(InteractionHandler.columnAt(10, 1, 10, offset) == 0)
    assert(InteractionHandler.columnAt(15, 1, 10, offset) == 5)
    assert(InteractionHandler.columnAt(0, 1, 10, offset) == 0) // left of content
    assert(InteractionHandler.columnAt(100, 1, 10, offset) == 9)
  }

  test("envYFromPixel subtracts offset") {
    assert(InteractionHandler.envYFromPixel(50, 10) == 40)
    assert(InteractionHandler.envYFromPixel(5, 10) == -5)
  }

  test("adjustFloor and adjustWaterSurface mutate the intended arrays") {
    val env = new Environment(8, 200)
    for (i <- 0 until env.width) {
      env.floor(i) = 100.0
      env.h0(i) = 50.0
      env.h1(i) = 50.0
    }
    env.adjustFloor(3, 7.0)
    env.adjustWaterSurface(4, -2.5)
    assert(math.abs(env.floor(3) - 107.0) < 1e-12)
    assert(math.abs(env.h1(4) - 47.5) < 1e-12)
    assert(math.abs(env.h1(3) - 50.0) < 1e-12)
    assert(math.abs(env.floor(4) - 100.0) < 1e-12)
  }
}
