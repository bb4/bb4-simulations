package com.barrybecker4.simulation.water.model

import org.scalatest.funsuite.AnyFunSuite

class EnvironmentSuite extends AnyFunSuite {

  test("reset fills finite heights and floor") {
    val env = new Environment(32, 400)
    env.reset()
    for (i <- 0 until env.width) {
      assert(env.h0(i).isFinite)
      assert(env.h1(i).isFinite)
      assert(env.floor(i).isFinite)
    }
  }

  test("integrate produces finite state after reset") {
    val env = new Environment(64, 200)
    env.reset()
    env.integrate(0.0004)
    for (i <- 0 until env.width) {
      assert(env.h0(i).isFinite)
      assert(env.h1(i).isFinite)
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
}
