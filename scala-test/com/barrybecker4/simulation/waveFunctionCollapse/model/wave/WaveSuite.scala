// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.wave

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagation.PropagatorState
import org.scalatest.funsuite.AnyFunSuite

class WaveSuite extends AnyFunSuite {

  /** Each adjacency list has the same length so `clear` can fill compatibility counts. */
  private final class StubPropagator(tCounter: Int, compatLen: Int) extends PropagatorState {
    locally {
      for (d <- 0 to 3) {
        state(d) = Array.fill(tCounter)(null)
        for (t <- 0 until tCounter)
          state(d)(t) = Array.fill(compatLen)(0)
      }
    }
  }

  test("ban removes one option and records stack entry") {
    val w = new Wave(1, 1)
    val weights = Array(0.5, 0.5)
    w.init(2, weights)
    val stub = new StubPropagator(2, compatLen = 2)
    w.clear(2, weights, stub)

    assert(w.get(0).sumOfOnes == 2)
    w.ban(0, 0, weights)
    assert(w.get(0).sumOfOnes == 1)
    assert(!w.get(0).enabled(0))
    assert(w.get(0).enabled(1))
  }

  test("clear restores uniform domain after ban") {
    val w = new Wave(1, 1)
    val weights = Array(0.5, 0.5)
    w.init(2, weights)
    val stub = new StubPropagator(2, compatLen = 3)
    w.clear(2, weights, stub)
    w.ban(0, 0, weights)
    w.clear(2, weights, stub)
    assert(w.get(0).sumOfOnes == 2)
    assert(w.get(0).enabled(0) && w.get(0).enabled(1))
  }
}
