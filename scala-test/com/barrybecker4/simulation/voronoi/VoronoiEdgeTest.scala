// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.{Point, VoronoiEdge}
import org.scalatest.funsuite.AnyFunSuite

class VoronoiEdgeTest extends AnyFunSuite {

  private val eps = 1e-9

  test("intersection: perpendicular bisectors of square diagonals meet at center") {
    val e1 = new VoronoiEdge(Point(0, 0), Point(6, 6))
    val e2 = new VoronoiEdge(Point(0, 6), Point(6, 0))
    val p = e1.intersection(e2)
    assert(p != null)
    assert(math.abs(p.x - 3) < eps)
    assert(math.abs(p.y - 3) < eps)
  }

  test("intersection: one horizontal-site edge and one oblique edge") {
    val eVert = new VoronoiEdge(Point(0, 0), Point(4, 0)) // equal y → vertical bisector
    val eOb = new VoronoiEdge(Point(1, 1), Point(5, 3))
    val p = eVert.intersection(eOb)
    assert(p != null)
    assert(math.abs(p.x - 2) < eps)
  }

  test("same-x sites produce a horizontal bisector with finite slope params") {
    val e = new VoronoiEdge(Point(10, 10), Point(10, 50))
    assert(math.abs(e.m) < eps)
    assert(e.b.isFinite)
    assert(math.abs(e.b - 30.0) < eps) // midpoint y
  }

  test("same-x horizontal bisector intersects an oblique edge at a finite point") {
    val eHoriz = new VoronoiEdge(Point(10, 10), Point(10, 50))
    val eOb = new VoronoiEdge(Point(0, 0), Point(40, 40))
    val p = eHoriz.intersection(eOb)
    assert(p != null)
    assert(p.x.isFinite && p.y.isFinite)
    assert(math.abs(p.y - 30.0) < eps)
  }

  test("parallel non-intersecting vertical bisectors return null") {
    val e1 = new VoronoiEdge(Point(0, 0), Point(4, 0))
    val e2 = new VoronoiEdge(Point(0, 2), Point(4, 2))
    assert(e1.intersection(e2) == null)
  }
}
