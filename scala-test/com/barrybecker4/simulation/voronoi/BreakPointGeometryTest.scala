// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.{BreakPoint, Point, VoronoiEdge}
import org.scalatest.funsuite.AnyFunSuite

class BreakPointGeometryTest extends AnyFunSuite {

  private val eps = 1e-6

  test("pointWhenSitesHorizontal: x is midpoint of sites") {
    val s1 = Point(10, 20)
    val s2 = Point(30, 20)
    val sweepLoc = 5.0
    val p = BreakPoint.pointWhenSitesHorizontal(s1, s2, sweepLoc)
    assert(math.abs(p.x - 20) < eps)
    assert(p.y > sweepLoc)
  }

  test("pointWhenSitesGeneral: matches known construction for fixed edge") {
    val s1 = Point(10, 10)
    val s2 = Point(40, 50)
    val e = new VoronoiEdge(s1, s2)
    val sweepLoc = 8.0
    val p = BreakPoint.pointWhenSitesGeneral(s1, s2, e, sweepLoc)
    assert(!p.x.isNaN && !p.y.isNaN)
    assert(math.abs(e.m * p.x + e.b - p.y) < 1e-4)
  }
}
