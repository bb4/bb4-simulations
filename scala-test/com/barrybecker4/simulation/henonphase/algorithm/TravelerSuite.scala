// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase.algorithm

import java.awt.Color
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.funsuite.AnyFunSuite

class TravelerSuite extends AnyFunSuite {

  private val EPS = 1e-12
  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(EPS)

  test("increment one step with angle 0 maps as expected") {
    val params = TravelerParams(angle = 0.0, multiplier = 1.0, offset = 0.0)
    val t = new Traveler(0.5, 0.0, Color.BLACK, params)
    t.increment()
    assert(t.x === 0.5)
    assert(t.y === -0.25)
  }

  test("increment two steps with angle 0") {
    val params = TravelerParams(angle = 0.0, multiplier = 1.0, offset = 0.0)
    val t = new Traveler(0.5, 0.0, Color.BLACK, params)
    t.increment()
    t.increment()
    assert(t.x === 0.5)
    assert(t.y === -0.5)
  }
}
