// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import org.scalatest.funsuite.AnyFunSuite

class PointGeometryTest extends AnyFunSuite {

  private val eps = 1e-9

  test("ccw: counterclockwise turn") {
    val a = Point(0, 0)
    val b = Point(1, 0)
    val c = Point(1, 1)
    assert(Point.ccw(a, b, c) == 1)
  }

  test("ccw: clockwise turn") {
    val a = Point(0, 0)
    val b = Point(1, 0)
    val c = Point(1, -1)
    assert(Point.ccw(a, b, c) == -1)
  }

  test("ccw: collinear") {
    val a = Point(0, 0)
    val b = Point(1, 1)
    val c = Point(2, 2)
    assert(Point.ccw(a, b, c) == 0)
  }

  test("midpoint") {
    val m = Point.midpoint(Point(0, 0), Point(4, 10))
    assert(math.abs(m.x - 2) < eps)
    assert(math.abs(m.y - 5) < eps)
  }

  test("minYOrderedCompareTo: larger y sorts before smaller y in this ordering") {
    val low = Point(0, 10)
    val high = Point(100, 50)
    assert(Point.minYOrderedCompareTo(high, low) < 0)
  }

  test("equals/hashCode contract: equal points have equal hashCodes") {
    val a = Point(1.0, 2.0)
    val b = Point(1.0, 2.0)
    assert(a == b)
    assert(a.hashCode == b.hashCode)
  }

  test("equals/hashCode contract: near-epsilon-equal points agree on hashCode when equal") {
    val a = Point(1.0, 2.0)
    val b = Point(1.0 + 1e-10, 2.0)
    if (a == b) assert(a.hashCode == b.hashCode)
  }

  test("Map round-trip uses equals consistently with hashCode") {
    val key = Point(3.0, 4.0)
    val m = Map(key -> "site")
    assert(m.contains(Point(3.0, 4.0)))
  }
}
