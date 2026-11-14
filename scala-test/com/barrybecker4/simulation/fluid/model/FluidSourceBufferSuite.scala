// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import org.scalatest.funsuite.AnyFunSuite

/**
  * Mouse / source increments must land on the current field (Stam add_source),
  * so they survive the following diffuse/advect step instead of being overwritten.
  */
class FluidSourceBufferSuite extends AnyFunSuite {

  private def quietEnv(w: Int, h: Int): FluidEnvironment = {
    val env = new FluidEnvironment(w, h)
    env.setViscosity(0)
    env.setDiffusionRate(0)
    env.grid.clear()
    env
  }

  test("incrementU is visible on getU before a step") {
    val env = quietEnv(12, 12)
    env.incrementU(6, 6, 1.25)
    assert(env.getU(6, 6) === 1.25)
  }

  test("incrementDensity is visible on getValue before a step") {
    val env = quietEnv(12, 12)
    env.incrementDensity(5, 7, 3.5)
    // getValue maps render coords; density accessors use solver indices 1..N
    assert(env.getDensity(5, 7) === 3.5)
  }

  test("velocity impulse survives one step with zero viscosity") {
    val withForce = quietEnv(16, 16)
    val control = quietEnv(16, 16)
    withForce.incrementU(8, 8, 2.0)
    withForce.stepForward(0.03)
    control.stepForward(0.03)
    val injected = math.abs(withForce.getU(8, 8))
    val baseline = math.abs(control.getU(8, 8))
    assert(injected > baseline + 0.05, s"expected injected u=$injected to exceed control=$baseline")
  }

  test("density impulse survives one step with zero diffusion") {
    val withInk = quietEnv(16, 16)
    val control = quietEnv(16, 16)
    withInk.incrementDensity(8, 8, 5.0)
    withInk.stepForward(0.03)
    control.stepForward(0.03)
    val injected = withInk.getDensity(8, 8)
    val baseline = control.getDensity(8, 8)
    assert(injected > baseline + 1.0, s"expected density $injected to exceed control $baseline")
  }
}
