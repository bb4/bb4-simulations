// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import org.scalatest.funsuite.AnyFunSuite

/**
  * Regression tests for XML parsing (whitespace between elements must not create spurious walls).
  */
class ConditionsSuite extends AnyFunSuite {

  test("BasicConfigParsesExactlyOneWall") {
    val c = new Conditions(BASIC.fileName)
    assertResult(1) { c.getWalls.size }
    val w = c.getWalls.head
    assertResult(15.0) { w.getStartPoint.x }
    assertResult(10.0) { w.getStartPoint.y }
    assertResult(15.0) { w.getStopPoint.x }
    assertResult(15.0) { w.getStopPoint.y }
  }
}
