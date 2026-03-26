// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

import org.scalatest.funsuite.AnyFunSuite


class VerhulstMapTest extends AnyFunSuite {

  private val eps = 1e-12

  test("nextPopulation: fixed points at 0 and 1") {
    val r = 2.0
    assert(VerhulstMap.nextPopulation(0.0, r) == 0.0)
    assert(VerhulstMap.nextPopulation(1.0, r) == 1.0)
  }

  test("nextPopulation: one step matches hand computation") {
    val p0 = 0.1
    val r = 2.0
    val expected = 0.1 * (1.0 + 2.0 * 0.9)
    assert(math.abs(VerhulstMap.nextPopulation(p0, r) - expected) < eps)
  }

  test("nextPopulation: many steps with r=1 stay in [0, 1] and finite") {
    var p = Rabbits.INITIAL_NUM_RABBITS
    val r = 1.0
    (0 until 50).foreach { _ =>
      p = VerhulstMap.nextPopulation(p, r)
      assert(p >= 0.0 && p <= 1.0, s"population left [0,1]: p=$p")
      assert(!p.isNaN && !p.isInfinite)
    }
  }
}
