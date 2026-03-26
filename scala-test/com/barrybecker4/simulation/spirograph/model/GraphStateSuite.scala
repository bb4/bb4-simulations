// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.spirograph.model

import org.scalatest.funsuite.AnyFunSuite

class GraphStateSuite extends AnyFunSuite {

  test("getNumRevolutions matches gcd-based period for integer radii") {
    val state = new GraphState
    state.params.r1 = 60f
    state.params.r2 = 60f
    assert(state.getNumRevolutions === 1)

    state.params.r1 = 60f
    state.params.r2 = 40f
    assert(state.getNumRevolutions === 2)

    state.params.r1 = 12f
    state.params.r2 = 8f
    assert(state.getNumRevolutions === 2)
  }

  test("getNumRevolutions matches for negative r2") {
    val state = new GraphState
    state.params.r1 = 60f
    state.params.r2 = -40f
    assert(state.getNumRevolutions === 2)
  }

  test("getDelayMillis low velocity uses branch for velocity < VELOCITY_MAX/2") {
    val state = new GraphState
    state.setVelocity(30)
    // 5 * (120 + (60 - 30)) / 30 = 5 * 150 / 30 = 25
    assert(state.getDelayMillis === 25)
    state.setVelocity(1)
    assert(state.getDelayMillis === 5 * (120 + 59) / 1)
  }

  test("getDelayMillis mid velocity uses else branch") {
    val state = new GraphState
    state.setVelocity(60)
    // (5 * 120) / 60 - 5 = 5
    assert(state.getDelayMillis === 5)
  }

  test("getDelayMillis max velocity") {
    val state = new GraphState
    state.setVelocity(GraphState.VELOCITY_MAX)
    assert(state.getDelayMillis === 0)
  }
}
