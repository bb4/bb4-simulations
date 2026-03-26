// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase.algorithm

import org.scalatest.funsuite.AnyFunSuite

class TravelerParamsSuite extends AnyFunSuite {

  test("defaults match companion constants") {
    val p = TravelerParams()
    assert(p.angle == TravelerParams.DEFAULT_PHASE_ANGLE)
    assert(p.multiplier == TravelerParams.DEFAULT_MULTIPLIER)
    assert(p.offset == TravelerParams.DEFAULT_OFFSET)
  }

  test("copy updates one field") {
    val p = TravelerParams()
    val q = p.copy(angle = 1.0)
    assert(q.angle == 1.0)
    assert(q.multiplier == p.multiplier)
    assert(q.offset == p.offset)
  }

  test("usesExplicitMultiplier and usesExplicitOffset") {
    assert(!TravelerParams().usesExplicitMultiplier)
    assert(!TravelerParams().usesExplicitOffset)
    assert(TravelerParams(multiplier = 1.01).usesExplicitMultiplier)
    assert(TravelerParams(offset = 0.01).usesExplicitOffset)
  }

  test("case class equality") {
    val a = TravelerParams(angle = 1.0, multiplier = 1.0, offset = 0.0)
    val b = TravelerParams(angle = 1.0, multiplier = 1.0, offset = 0.0)
    val c = TravelerParams(angle = 2.0, multiplier = 1.0, offset = 0.0)
    assert(a == b)
    assert(a != c)
  }
}
