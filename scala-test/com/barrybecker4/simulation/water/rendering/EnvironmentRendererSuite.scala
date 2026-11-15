package com.barrybecker4.simulation.water.rendering

import org.scalatest.funsuite.AnyFunSuite

class EnvironmentRendererSuite extends AnyFunSuite {

  test("velocityStrokeY points downward when h1 increases (surface drops in screen y)") {
    val (y0, y1) = EnvironmentRenderer.velocityStrokeY(surfaceY = 40.0, h0 = 40.0, h1 = 50.0, scale = 1.0)
    assert(y1 > y0)
  }

  test("velocityStrokeY points upward when h1 decreases") {
    val (y0, y1) = EnvironmentRenderer.velocityStrokeY(surfaceY = 50.0, h0 = 50.0, h1 = 40.0, scale = 1.0)
    assert(y1 < y0)
  }

  test("velocityStrokeY is zero-length when h0 equals h1") {
    val (y0, y1) = EnvironmentRenderer.velocityStrokeY(surfaceY = 30.0, h0 = 30.0, h1 = 30.0, scale = 8.0)
    assert(y0 == y1)
  }

  test("RenderingOptions showVelocities defaults to false and is mutable") {
    val opts = new RenderingOptions
    assert(!opts.showVelocities)
    opts.showVelocities = true
    assert(opts.showVelocities)
  }
}
