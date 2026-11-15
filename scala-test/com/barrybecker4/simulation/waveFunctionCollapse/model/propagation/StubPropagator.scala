// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.propagation

import com.barrybecker4.simulation.waveFunctionCollapse.model.IntArray

/** Shared test propagator: every tile lists `compatLen` dummy neighbors in each direction. */
final class StubPropagator(tCounter: Int, compatLen: Int) extends PropagatorState {
  for (d <- 0 to 3) {
    state(d) = Array.fill(tCounter)(null)
    for (t <- 0 until tCounter)
      state(d)(t) = Array.fill(compatLen)(0)
  }
}

/**
  * Explicit adjacency tables for propagation unit tests.
  * `compat(d)(t)` = tile indices allowed at the neighbor in direction `d` when this cell has tile `t`.
  */
final class ExplicitPropagator(compat: Array[Array[IntArray]]) extends PropagatorState {
  require(compat.length == 4)
  for (d <- 0 to 3) {
    state(d) = compat(d)
  }
}

object ExplicitPropagator {

  /** Each tile is only compatible with itself in every direction (independent 1-cycles). */
  def selfOnly(tCounter: Int): ExplicitPropagator = {
    val compat = Array.tabulate(4) { _ =>
      Array.tabulate(tCounter)(t => Array(t))
    }
    new ExplicitPropagator(compat)
  }
}
