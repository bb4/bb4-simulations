// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.spirograph.model

import org.scalatest.funsuite.AnyFunSuite

class ParametersSuite extends AnyFunSuite {

  private val Eps = 1e-4f

  test("sign matches SpirographMath") {
    val p = new Parameters
    p.r2 = -40f
    assert(p.sign === SpirographMath.sign(-40f))
    p.r2 = 40f
    assert(p.sign === SpirographMath.sign(40f))
  }

  test("getCenter matches SpirographMath") {
    val p = new Parameters
    p.r1 = 60f
    p.r2 = 40f
    p.theta = 0.3f
    val c = p.getCenter(400, 300)
    val (cx, cy) = SpirographMath.center(400, 300, p.r1, p.r2, p.theta)
    assert(math.abs(c.getX - cx) < Eps)
    assert(math.abs(c.getY - cy) < Eps)
  }

  test("initialize sets pen to theta-0 position (no stray first segment)") {
    val p = new Parameters
    p.r1 = 60f
    p.r2 = 60f
    p.pos = 60f
    val w = 400
    val h = 300
    p.initialize(w, h)
    val (cx, cy) = SpirographMath.center(w, h, p.r1, p.r2, 0f)
    val (px, py) = SpirographMath.penPoint(cx, cy, p.pos, 0f)
    assert(math.abs(p.x - px) < Eps, s"x expected=$px actual=${p.x}")
    assert(math.abs(p.y - py) < Eps, s"y expected=$py actual=${p.y}")
    assert(p.theta === 0f)
    assert(p.phi === 0f)
  }

  test("initialize with negative r2 matches math at theta 0") {
    val p = new Parameters
    p.r1 = 60f
    p.r2 = -40f
    p.pos = 30f
    val w = 200
    val h = 200
    p.initialize(w, h)
    val (cx, cy) = SpirographMath.center(w, h, p.r1, p.r2, 0f)
    val (px, py) = SpirographMath.penPoint(cx, cy, p.pos, 0f)
    assert(math.abs(p.x - px) < Eps)
    assert(math.abs(p.y - py) < Eps)
  }
}
