// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class VoronoiProcessorTest extends AnyFunSuite {

  private def finiteEndpoints(proc: VoronoiProcessor): Boolean =
    proc.getEdgeList.forall { e =>
      (e.p1 == null || (e.p1.x.isFinite && (e.p1.y.isFinite || e.p1.y.isInfinite))) &&
        (e.p2 == null || (e.p2.x.isFinite && (e.p2.y.isFinite || e.p2.y.isInfinite))) &&
        e.m.isFinite && e.b.isFinite
    }

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

  test("processAll completes for two sites with the same y") {
    val sites = IndexedSeq(Point(40, 100), Point(160, 100))
    val proc = new VoronoiProcessor(sites)
    proc.processAll()
    assert(proc.getEdgeList.nonEmpty)
    assert(finiteEndpoints(proc))
  }

  test("processAll completes for two sites with the same x") {
    val sites = IndexedSeq(Point(100, 40), Point(100, 160))
    val proc = new VoronoiProcessor(sites)
    proc.processAll()
    assert(proc.getEdgeList.nonEmpty)
    assert(finiteEndpoints(proc))
  }

  test("coincident sites are rejected") {
    val sites = IndexedSeq(Point(50, 50), Point(50, 50))
    assertThrows[IllegalArgumentException] {
      new VoronoiProcessor(sites)
    }
  }

  test("processAll completes for four cocircular sites") {
    // Square vertices lie on a common circle — stresses circle-event chains
    val sites = IndexedSeq(
      Point(100, 100),
      Point(200, 100),
      Point(200, 200),
      Point(100, 200)
    )
    val proc = new VoronoiProcessor(sites)
    proc.processAll()
    assert(proc.getEdgeList.nonEmpty)
    assert(finiteEndpoints(proc))
  }

  test("processAll completes for collinear triple") {
    val sites = IndexedSeq(
      Point(50, 50),
      Point(100, 100),
      Point(150, 150)
    )
    val proc = new VoronoiProcessor(sites)
    proc.processAll()
    assert(proc.getEdgeList.nonEmpty)
    assert(finiteEndpoints(proc))
  }

  test("processAll completes for small random site sets without NaN edges") {
    val rnd = new Random(42L)
    for (n <- Seq(5, 10, 20)) {
      val sites = (0 until n).map { _ =>
        Point(10 + rnd.nextDouble() * 180, 10 + rnd.nextDouble() * 180)
      }.distinct
      val proc = new VoronoiProcessor(sites)
      proc.processAll()
      assert(proc.getEdgeList.nonEmpty, s"n=$n expected edges")
      assert(proc.getEdgeList.length <= 3 * sites.length + 10, s"n=$n edge bound")
      assert(finiteEndpoints(proc), s"n=$n finite endpoints/slopes")
    }
  }

}
