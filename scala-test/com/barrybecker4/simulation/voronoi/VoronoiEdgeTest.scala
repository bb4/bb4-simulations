// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.{Point, VoronoiEdge}
import org.scalatest.funsuite.AnyFunSuite

class VoronoiEdgeTest extends AnyFunSuite {

  private val eps = 1e-9

  test("intersection: two non-vertical bisectors meet at a point") {
    val e1 = new VoronoiEdge(Point(0, 0), Point(4, 0))
    val e2 = new VoronoiEdge(Point(0, 4), Point(4, 4))
    val p = e1.intersection(e2)
    assert(p != null)
    assert(math.abs(p.x - 2) < eps)
    assert(math.abs(p.y - 2) < eps)
  }

  test("intersection: parallel distinct lines yield null") {
    val e1 = new VoronoiEdge(Point(0, 0), Point(2, 0))
    val e2 = new VoronoiEdge(Point(0, 2), Point(2, 2))
    assert(e1.intersection(e2) == null)
  }

  test("intersection: one vertical bisector (sites share y) and one oblique") {
    val eVert = new VoronoiEdge(Point(0, 0), Point(4, 0))
    val eOther = new VoronoiEdge(Point(1, 1), Point(5, 3))
    val p = eVert.intersection(eOther)
    assert(p != null)
    assert(math.abs(p.x - 2) < eps)
  }
}
