// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import org.scalatest.funsuite.AnyFunSuite

class FluidResizeSuite extends AnyFunSuite {

  test("setSize with same dimensions preserves field values") {
    val env = new FluidEnvironment(10, 10)
    env.grid.clear()
    env.incrementDensity(4, 5, 7.0)
    env.incrementU(4, 5, 1.5)
    env.setSize(10, 10)
    assert(env.getDensity(4, 5) === 7.0)
    assert(env.getU(4, 5) === 1.5)
  }

  test("setSize with different dimensions reallocates a fresh grid") {
    val env = new FluidEnvironment(10, 10)
    env.grid.clear()
    env.incrementDensity(4, 5, 7.0)
    env.setSize(14, 12)
    assert(env.getWidth === 14)
    assert(env.getHeight === 12)
    // New grid is re-seeded; the old impulse must not carry over
    val fresh = new FluidEnvironment(14, 12)
    assert(env.getDensity(4, 5) === fresh.getDensity(4, 5))
    assert(env.getDensity(4, 5) !== 7.0)
  }
}
