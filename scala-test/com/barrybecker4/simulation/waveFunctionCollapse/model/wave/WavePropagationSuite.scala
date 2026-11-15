// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.wave

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagation.ExplicitPropagator
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class WavePropagationSuite extends AnyFunSuite {

  private val neverBoundary: (Int, Int) => Boolean = (_, _) => false

  test("propagate bans neighbor when self-only adjacency loses support") {
    // 1x2 grid: ban tile 0 from cell 0 → cell 1 must lose tile 0 via east compatibility.
    val w = new Wave(2, 1, allowInconsistencies = false)
    val weights = Array(1.0, 1.0)
    w.init(2, weights)
    val prop = ExplicitPropagator.selfOnly(2)
    w.clear(2, weights, prop)

    w.ban(0, 0, weights)
    w.propagate(neverBoundary, weights, prop)

    assert(!w.get(1).enabled(0))
    assert(w.get(1).enabled(1))
    assert(w.get(1).sumOfOnes == 1)
  }

  test("observe returns Some(false) on contradiction") {
    val w = new Wave(1, 1, allowInconsistencies = false)
    val weights = Array(1.0, 1.0)
    w.init(2, weights)
    val prop = ExplicitPropagator.selfOnly(2)
    w.clear(2, weights, prop)
    w.ban(0, 0, weights)
    w.ban(0, 1, weights)

    val result = w.observe(2, weights, neverBoundary, new Random(0))
    assert(result.contains(false))
  }

  test("periodic wrap propagates ban across left edge") {
    // 3x1 periodic: ban tile 0 at cell 0; west neighbor of cell 0 is cell 2.
    val w = new Wave(3, 1, allowInconsistencies = false)
    val weights = Array(1.0, 1.0)
    w.init(2, weights)
    val prop = ExplicitPropagator.selfOnly(2)
    w.clear(2, weights, prop)

    w.ban(0, 0, weights)
    w.propagate(neverBoundary, weights, prop)

    assert(!w.get(2).enabled(0), "west wrap from cell 0 should ban tile 0 at cell 2")
    assert(w.get(2).enabled(1))
  }

  test("allowInconsistencies prevents ban of last remaining option") {
    val w = new Wave(1, 1, allowInconsistencies = true)
    val weights = Array(1.0, 1.0)
    w.init(2, weights)
    val prop = ExplicitPropagator.selfOnly(2)
    w.clear(2, weights, prop)
    w.ban(0, 0, weights)
    assert(w.get(0).sumOfOnes == 1)

    w.ban(0, 1, weights)
    assert(w.get(0).sumOfOnes == 1)
    assert(w.get(0).enabled(1))
  }

  test("without allowInconsistencies last ban empties domain") {
    val w = new Wave(1, 1, allowInconsistencies = false)
    val weights = Array(1.0, 1.0)
    w.init(2, weights)
    val prop = ExplicitPropagator.selfOnly(2)
    w.clear(2, weights, prop)
    w.ban(0, 0, weights)
    w.ban(0, 1, weights)
    assert(w.get(0).sumOfOnes == 0)
    assert(!w.get(0).enabled(0) && !w.get(0).enabled(1))
  }

  test("onBoundary cells are skipped for entropy and wrap is not applied into boundary") {
    val w = new Wave(2, 1, allowInconsistencies = false)
    val weights = Array(1.0, 1.0)
    w.init(2, weights)
    val prop = ExplicitPropagator.selfOnly(2)
    w.clear(2, weights, prop)

    // Treat x>=1 as outside / boundary so cell 1 is never observed and east of 0 is boundary.
    val onBoundary: (Int, Int) => Boolean = (x, _) => x < 0 || x >= 1
    w.ban(0, 0, weights)
    w.propagate(onBoundary, weights, prop)

    // Cell 1 is out of play; ban must not have reached it via wrap or east step.
    assert(w.get(1).enabled(0) && w.get(1).enabled(1))

    val result = w.observe(2, weights, onBoundary, new Random(1))
    // Only cell 0 is interior; after ban it has one option left → collapse completes.
    assert(result.contains(true) || result.isEmpty)
  }
}
