// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.spirograph.model

import org.scalatest.funsuite.AnyFunSuite

class SpirographMathSuite extends AnyFunSuite {

  /** Float trig accumulates a little error over a full period. */
  private val Eps = 1e-3f

  private def assertNear(actual: Float, expected: Float, msg: String = ""): Unit =
    assert(math.abs(actual - expected) < Eps, s"$msg expected=$expected actual=$actual")

  private def assertNearD(actual: Double, expected: Double, msg: String = ""): Unit =
    assert(math.abs(actual - expected) < Eps, s"$msg expected=$expected actual=$actual")

  test("sign is -1 for negative r2 and 1 otherwise") {
    assert(SpirographMath.sign(-40f) === -1)
    assert(SpirographMath.sign(0f) === 1)
    assert(SpirographMath.sign(40f) === 1)
  }

  test("numRevolutions matches gcd-based period") {
    assert(SpirographMath.numRevolutions(60f, 60f) === 1)
    assert(SpirographMath.numRevolutions(60f, 40f) === 2)
    assert(SpirographMath.numRevolutions(12f, 8f) === 2)
    assert(SpirographMath.numRevolutions(60f, -40f) === 2)
  }

  test("segmentsPerPass and segmentLimit grow with |pos/r2|") {
    val n1 = SpirographMath.segmentsPerPass(200, 60f, 60f)
    val n2 = SpirographMath.segmentsPerPass(200, 120f, 60f)
    assertNear(n1, 1f + 200f)
    assertNear(n2, 1f + 400f)
    assert(SpirographMath.segmentLimit(n2, 2) > SpirographMath.segmentLimit(n1, 2))
  }

  test("theta at count 0 is 0; phi matches roll ratio") {
    assertNear(SpirographMath.theta(0, 201f), 0f)
    val th = SpirographMath.theta(50, 200f)
    assertNear(SpirographMath.phi(th, 60f, 60f), th * 2f)
  }

  test("center and penPoint at theta 0 with defaults r1=r2=pos=60") {
    val w = 400
    val h = 300
    val r1 = 60f
    val r2 = 60f
    val pos = 60f
    val (cx, cy) = SpirographMath.center(w, h, r1, r2, 0f)
    assertNearD(cx, (w >> 1) + (r1 + r2))
    assertNearD(cy, (h >> 1).toDouble)
    val (px, py) = SpirographMath.penPoint(cx, cy, pos, 0f)
    assertNear(px, (cx + pos).toFloat)
    assertNear(py, cy.toFloat)
  }

  test("curve closes for integer radii after full segmentLimit") {
    val r1 = 60f
    val r2 = 40f
    val pos = 60f
    val w = 400
    val h = 300
    val revs = SpirographMath.numRevolutions(r1, r2)
    val n = SpirographMath.segmentsPerPass(200, pos, r2)
    val limit = SpirographMath.segmentLimit(n, revs)
    val (x0, y0) = pointAt(w, h, r1, r2, pos, 0, n)
    val (xEnd, yEnd) = pointAt(w, h, r1, r2, pos, limit, n)
    assertNear(x0, xEnd, "x closure")
    assertNear(y0, yEnd, "y closure")
  }

  test("sample points in slider range are finite") {
    val samples = Seq(
      (5f, 5f, 10f),
      (255f, 200f, 300f),
      (60f, -40f, -100f),
      (100f, 1f, 0f)
    )
    for ((r1, r2, pos) <- samples if r2 != 0f) {
      val th = SpirographMath.theta(10, 200f)
      val ph = SpirographMath.phi(th, r1, r2)
      val (cx, cy) = SpirographMath.center(200, 200, r1, r2, th)
      val (px, py) = SpirographMath.penPoint(cx, cy, pos, ph)
      assert(px.isFinite && py.isFinite, s"non-finite for r1=$r1 r2=$r2 pos=$pos")
    }
  }

  private def pointAt(w: Int, h: Int, r1: Float, r2: Float, pos: Float, count: Int, n: Float): (Float, Float) = {
    val th = SpirographMath.theta(count, n)
    val ph = SpirographMath.phi(th, r1, r2)
    val (cx, cy) = SpirographMath.center(w, h, r1, r2, th)
    SpirographMath.penPoint(cx, cy, pos, ph)
  }
}
