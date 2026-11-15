// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.placement.method.{GaussianPlacement, PoissonGrid, PoissonPlacement, PoissonParams, RandomPlacement}
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class PlacementMethodTest extends AnyFunSuite {

  test("RandomPlacement.increment adds the requested number of points") {
    val rnd = new Random(1L)
    val placement = new RandomPlacement(200, 200, 10, 100, rnd)
    placement.increment(7)
    assert(placement.getSamples.length == 7)
  }

  test("GaussianPlacement.increment adds the requested number of points") {
    val rnd = new Random(1L)
    val placement = new GaussianPlacement(200, 200, 10, 100, rnd)
    placement.increment(5)
    assert(placement.getSamples.length == 5)
  }

  test("PoissonGrid samples respect minimum separation roughly equal to radius") {
    val rnd = new Random(0L)
    val radius = 15.0
    val g = PoissonGrid(200.0, 200.0, 20.0, radius, rnd)
    g.addSample(com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point(100, 100))
    var added = 0
    var i = 0
    while (i < 40 && added < 20) {
      val idx = g.addNewElementIfPossible(0, 30)
      if (idx >= 0) added += 1
      i += 1
    }
    val pts = g.samples
    for (a <- pts.indices; b <- (a + 1) until pts.length) {
      val d = pts(a).distanceTo(pts(b))
      assert(d + 1e-9 >= radius, s"points too close: $d < $radius")
    }
  }

  test("PoissonPlacement.increment grows sample count") {
    val rnd = new Random(2L)
    val params = PoissonParams(12.0, 20)
    val placement = new PoissonPlacement(200, 200, 15, params, 50, rnd)
    placement.increment(5)
    assert(placement.getSamples.nonEmpty)
    assert(placement.getSamples.length <= 50)
  }
}
