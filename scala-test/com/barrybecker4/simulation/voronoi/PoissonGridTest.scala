// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.placement.method.PoissonGrid
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class PoissonGridTest extends AnyFunSuite {

  test("addSample places index in grid and grows samples") {
    val rnd = new Random(0L)
    val g = PoissonGrid(200.0, 200.0, 10.0, 20.0, rnd)
    val i0 = g.addSample(Point(100, 100))
    assert(i0 == 0)
    assert(g.samples.length == 1)
    val i1 = g.addSample(Point(150, 150))
    assert(i1 == 1)
    assert(g.samples.length == 2)
  }

}
