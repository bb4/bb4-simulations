// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.model

import org.scalatest.funsuite.AnyFunSuite

class FluidEnvironmentStepSuite extends AnyFunSuite {

  private def isFinite(v: Double): Boolean = !v.isNaN && !v.isInfinite

  private def allFinite(env: FluidEnvironment): Boolean = {
    val w = env.getWidth
    val h = env.getHeight
    (1 to w).forall { i =>
      (1 to h).forall { j =>
        isFinite(env.getU(i, j)) && isFinite(env.getV(i, j)) && isFinite(env.getDensity(i, j))
      }
    }
  }

  test("quiescent field with zero viscosity and diffusion stays near zero") {
    val env = new FluidEnvironment(10, 10)
    env.setViscosity(0)
    env.setDiffusionRate(0)
    env.grid.clear()
    for (_ <- 1 to 5) env.stepForward(0.03)
    val maxAbs =
      (for (i <- 1 to env.getWidth; j <- 1 to env.getHeight)
        yield math.max(math.abs(env.getU(i, j)),
          math.max(math.abs(env.getV(i, j)), math.abs(env.getDensity(i, j))))).max
    assert(maxAbs < 1e-9, s"max abs field $maxAbs")
  }

  test("several steps with defaults stay finite") {
    val env = new FluidEnvironment(20, 16)
    env.incrementU(10, 8, 1.0)
    env.incrementDensity(10, 8, 2.0)
    for (_ <- 1 to 30) env.stepForward(0.03)
    assert(allFinite(env))
  }

  test("stepForward returns the same timestep") {
    val env = new FluidEnvironment(8, 8)
    assert(env.stepForward(0.05) === 0.05)
  }
}
