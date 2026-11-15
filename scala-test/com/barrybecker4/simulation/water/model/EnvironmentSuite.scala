package com.barrybecker4.simulation.water.model

import com.barrybecker4.simulation.water.ui.WaterSimulator
import org.scalatest.funsuite.AnyFunSuite

class EnvironmentSuite extends AnyFunSuite {

  private val dt = WaterSimulator.INITIAL_TIME_STEP

  test("reset fills finite heights and floor") {
    val env = new Environment(32, 400)
    env.reset()
    for (i <- 0 until env.width) {
      assert(env.h0(i).isFinite)
      assert(env.h1(i).isFinite)
      assert(env.floor(i).isFinite)
    }
  }

  test("initial profile has water below 400px and scales with height") {
    val h = 200
    val envH = new Environment(64, h)
    val env2H = new Environment(64, 2 * h)

    assert(envH.floor.indices.exists(i => envH.floor(i) > envH.h1(i)))

    for (i <- 0 until envH.width) {
      assert(math.abs(env2H.h1(i) - 2.0 * envH.h1(i)) < 1e-9, s"h1 at $i")
      assert(math.abs(env2H.floor(i) - 2.0 * envH.floor(i)) < 1e-9, s"floor at $i")
    }
  }

  test("integrate produces finite state after reset") {
    val env = new Environment(64, 200)
    env.reset()
    env.integrate(dt)
    for (i <- 0 until env.width) {
      assert(env.h0(i).isFinite)
      assert(env.h1(i).isFinite)
    }
  }

  test("rest state is unchanged after several integrate steps") {
    val env = new Environment(16, 200)
    for (i <- 0 until env.width) {
      env.floor(i) = 100.0
      env.h0(i) = 40.0
      env.h1(i) = 40.0
    }
    val before = env.h1.clone()
    for (_ <- 0 until 5) env.integrate(dt)
    for (i <- 0 until env.width) {
      assert(math.abs(env.h1(i) - before(i)) < 1e-9, s"h1 at $i")
      assert(math.abs(env.h0(i) - before(i)) < 1e-9, s"h0 at $i")
    }
  }

  test("integrate preserves volume of a single connected pool") {
    val env = new Environment(16, 200)
    for (i <- 0 until env.width) {
      env.floor(i) = 100.0
      env.h0(i) = 50.0
      env.h1(i) = 50.0
    }
    // gentle surface perturbation still one connected wet region
    env.h1(5) = 45.0
    env.h0(5) = 45.0
    env.h1(6) = 48.0
    env.h0(6) = 48.0

    def volume: Double =
      (0 until env.width).map(i => math.max(0.0, env.floor(i) - env.h1(i))).sum

    val before = volume
    env.integrate(dt)
    val after = volume
    assert(math.abs(after - before) < 1e-6, s"volume before=$before after=$after")
  }

  test("perturbed surface stays finite over many integrate steps") {
    val env = new Environment(64, 200)
    env.reset()
    for (i <- 0 until env.width by 7) {
      env.h1(i) = env.h1(i) - 5.0
      env.h0(i) = env.h1(i)
    }
    for (_ <- 0 until 50) env.integrate(dt)
    for (i <- 0 until env.width) {
      assert(env.h0(i).isFinite, s"h0 at $i")
      assert(env.h1(i).isFinite, s"h1 at $i")
      assert(env.floor(i).isFinite, s"floor at $i")
    }
  }

  test("runConservationStep adjusts pool without division when ct is zero for skipped regions") {
    val env = new Environment(8, 200)
    for (i <- 0 until 8) {
      env.floor(i) = 100.0
      env.h0(i) = 50.0
      env.h1(i) = 50.0
    }
    env.h1(3) = 40.0
    env.h1(4) = 40.0
    env.h0(3) = 50.0
    env.h0(4) = 50.0

    env.runConservationStep()

    assert(env.h1(3).isFinite)
    assert(env.h1(4).isFinite)
    assert(env.h1(3) <= env.floor(3) + 1e-5)
  }

  test("conservation handles more than 31 disconnected wet islands") {
    // Alternating wet/dry columns: even = wet, odd = dry → 40 wet islands (> MAX_CON 31)
    val width = 80
    val env = new Environment(width, 200)
    for (i <- 0 until width) {
      env.floor(i) = 100.0
      if (i % 2 == 0) {
        env.h0(i) = 50.0
        env.h1(i) = 50.0
      } else {
        env.h0(i) = 100.0
        env.h1(i) = 100.0
      }
    }
    env.runConservationStep()
    for (i <- 0 until width) {
      assert(env.h0(i).isFinite, s"h0 at $i")
      assert(env.h1(i).isFinite, s"h1 at $i")
    }
  }

  test("integrate preserves total volume across two separated pools") {
    val env = new Environment(20, 200)
    for (i <- 0 until env.width) {
      env.floor(i) = 100.0
      env.h0(i) = 100.0
      env.h1(i) = 100.0
    }
    // Left pool columns 1-4, right pool columns 12-15; dry land between
    for (i <- 1 to 4) {
      env.h0(i) = 50.0
      env.h1(i) = 50.0
    }
    for (i <- 12 to 15) {
      env.h0(i) = 60.0
      env.h1(i) = 60.0
    }
    // Mild surface perturbation in each pool
    env.h1(2) = 45.0
    env.h0(2) = 45.0
    env.h1(13) = 55.0
    env.h0(13) = 55.0

    def volume: Double =
      (0 until env.width).map(i => math.max(0.0, env.floor(i) - env.h1(i))).sum

    val before = volume
    env.integrate(dt)
    val after = volume
    assert(math.abs(after - before) < 1e-6, s"volume before=$before after=$after")
  }

  test("large time step keeps state finite") {
    val largeDt = 0.05
    val env = new Environment(32, 200)
    env.reset()
    for (i <- 0 until env.width by 5) {
      env.h1(i) = env.h1(i) - 3.0
      env.h0(i) = env.h1(i)
    }
    for (_ <- 0 until 30) env.integrate(largeDt)
    for (i <- 0 until env.width) {
      assert(env.h0(i).isFinite, s"h0 at $i")
      assert(env.h1(i).isFinite, s"h1 at $i")
      assert(env.floor(i).isFinite, s"floor at $i")
    }
  }
}
