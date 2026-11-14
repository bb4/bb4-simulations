// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import org.scalatest.funsuite.AnyFunSuite

/**
  * Non-square grids must scale advect/project by axis (dtx=dt*width, dty=dt*height).
  */
class FluidRectangularSuite extends AnyFunSuite {

  private def argmaxDensity(env: FluidEnvironment): (Int, Int) = {
    var best = (1, 1)
    var bestVal = Double.NegativeInfinity
    for (i <- 1 to env.getWidth; j <- 1 to env.getHeight) {
      val d = env.getDensity(i, j)
      if (d > bestVal) {
        bestVal = d
        best = (i, j)
      }
    }
    best
  }

  test("uniform vertical velocity advects density using height, not width") {
    val width = 40
    val height = 20
    val dt = 0.1
    val v = 1.0
    val env = new FluidEnvironment(width, height)
    env.setViscosity(0)
    env.setDiffusionRate(0)
    env.grid.clear()

    // Divergence-free uniform vertical field
    for (i <- 1 to width; j <- 1 to height) env.incrementV(i, j, v)
    val cx = width / 2
    val cy = height / 2
    env.incrementDensity(cx, cy, 50.0)

    env.stepForward(dt)

    val (px, py) = argmaxDensity(env)
    val expectedY = cy + (dt * height * v).round.toInt
    // Wrong code uses width for both axes → peak near cy + dt*width*v
    val wrongY = cy + (dt * width * v).round.toInt
    assert(px === cx, s"x should stay near $cx, got $px")
    assert(math.abs(py - expectedY) <= 1,
      s"peak y=$py expected ~$expectedY (height-scaled); width-scaled would be ~$wrongY")
    assert(math.abs(py - wrongY) > 1,
      s"peak y=$py should not match width-scaled advection ~$wrongY")
  }

  test("non-square steps stay finite under horizontal impulse") {
    val env = new FluidEnvironment(48, 24)
    env.setViscosity(1.0)
    env.grid.clear()
    env.incrementU(24, 12, 3.0)
    env.incrementDensity(24, 12, 4.0)
    for (_ <- 1 to 20) env.stepForward(0.03)
    for (i <- 1 to env.getWidth; j <- 1 to env.getHeight) {
      assert(!env.getU(i, j).isNaN)
      assert(!env.getV(i, j).isNaN)
      assert(!env.getDensity(i, j).isNaN)
    }
  }
}
