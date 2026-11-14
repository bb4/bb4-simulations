// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import org.scalatest.funsuite.AnyFunSuite

/**
  * True Gauss–Seidel makes viscosity scale with W·H. Defaults must stay in the light range
  * so the sim keeps flowing from the initial seed.
  */
class FluidDefaultViscositySuite extends AnyFunSuite {

  private val TimeStep = 0.03

  private def maxAbsVel(env: FluidEnvironment): Double = {
    var m = 0.0
    for (i <- 1 to env.getWidth; j <- 1 to env.getHeight)
      m = math.max(m, math.max(math.abs(env.getU(i, j)), math.abs(env.getV(i, j))))
    m
  }

  test("default viscosity keeps initial seed motion after several steps") {
    val env = new FluidEnvironment(80, 60)
    val before = maxAbsVel(env)
    assert(before > 0.1)
    for (_ <- 1 to 10) env.stepForward(TimeStep)
    val after = maxAbsVel(env)
    assert(after > 0.05, s"velocity collapsed under default viscosity: before=$before after=$after")
  }

  test("default viscosity is in the light 0–0.01 band") {
    assert(FluidEnvironment.DEFAULT_VISCOSITY > 0.0)
    assert(FluidEnvironment.DEFAULT_VISCOSITY <= 0.01)
  }

  test("legacy viscosity 10 kills motion in one step on a window-sized grid") {
    val env = new FluidEnvironment(80, 60)
    env.setViscosity(10.0)
    env.stepForward(TimeStep)
    assert(maxAbsVel(env) < 0.01)
  }
}
