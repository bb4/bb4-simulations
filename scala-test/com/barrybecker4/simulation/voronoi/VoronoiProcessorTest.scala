// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import org.scalatest.funsuite.AnyFunSuite

class VoronoiProcessorTest extends AnyFunSuite {

  test("processAll completes for two sites") {
    val sites = IndexedSeq(Point(10, 10), Point(50, 50))
    val proc = new VoronoiProcessor(sites)
    proc.processAll()
    assert(proc.getEdgeList.nonEmpty)
  }

  test("processAll completes for three sites in general position") {
    val sites = IndexedSeq(
      Point(100, 100),
      Point(200, 100),
      Point(150, 180)
    )
    val proc = new VoronoiProcessor(sites)
    proc.processAll()
    val edges = proc.getEdgeList
    assert(edges.nonEmpty)
    assert(edges.length <= 24)
  }

}
