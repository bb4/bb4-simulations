// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave

import com.barrybecker4.math.Range
import com.barrybecker4.simulation.cave.rendering.CaveColorMap
import org.scalatest.funsuite.AnyFunSuite

class CaveColorMapSuite extends AnyFunSuite {

  test("ControlPointsCountAndSpanRange") {
    val r = Range(0.1, 0.95)
    val pts = CaveColorMap.getControlPoints(r)
    assert(pts.length == 6)
    assert(pts.head == r.min)
    assert(math.abs(pts.last - (r.min + r.getExtent)) < 1e-12)
  }

  test("CreatesColormapWithoutThrowing") {
    val cm = new CaveColorMap(Range(0, 1.0))
    assert(cm != null)
  }
}
