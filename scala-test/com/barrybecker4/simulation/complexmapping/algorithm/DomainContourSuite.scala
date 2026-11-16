/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import org.scalatest.funsuite.AnyFunSuite


class DomainContourSuite extends AnyFunSuite {

  test("edgeCrossing finds midpoint when values are opposite") {
    val crossing = DomainContour.edgeCrossing((0.0, 0.0, -1.0), (2.0, 0.0, 1.0))
    assert(crossing.isDefined)
    assert(Math.abs(crossing.get.x - 1.0) < 1e-9)
    assert(Math.abs(crossing.get.y) < 1e-9)
  }

  test("edgeCrossing is empty when signs match") {
    assert(DomainContour.edgeCrossing((0.0, 0.0, 1.0), (1.0, 0.0, 2.0)).isEmpty)
  }

  test("zeroContours finds a horizontal zero crossing") {
    // Field is y - 0.5 on a 2x2 cell covering [0,1]x[0,1] → zero along y=0.5
    val xs = Array(0.0, 1.0)
    val ys = Array(0.0, 1.0)
    val values = Array(
      Array(-0.5, 0.5),
      Array(-0.5, 0.5)
    )
    val segs = DomainContour.zeroContours(values, xs, ys)
    assert(segs.nonEmpty)
    assert(segs.forall(s => Math.abs(s.a.y - 0.5) < 1e-9 && Math.abs(s.b.y - 0.5) < 1e-9))
  }
}
