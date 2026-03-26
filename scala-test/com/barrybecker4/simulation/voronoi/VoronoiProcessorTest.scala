// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import org.scalatest.funsuite.AnyFunSuite

class VoronoiProcessorTest extends AnyFunSuite {

  test("processAll completes for four sites in general position") {
    val sites = IndexedSeq(
      Point(50, 50),
      Point(250, 50),
      Point(250, 250),
      Point(50, 250)
    )
    val proc = new VoronoiProcessor(sites)
    proc.processAll()
    val edges = proc.getEdgeList
    assert(edges.nonEmpty)
    assert(edges.length >= 4)
    assert(edges.length <= 32)
  }

  test("processAll completes for three collinear sites (stress degeneracy)") {
    val sites = IndexedSeq(Point(10, 100), Point(50, 100), Point(90, 100))
    val proc = new VoronoiProcessor(sites)
    proc.processAll()
    assert(proc.getEdgeList.nonEmpty)
  }
}
