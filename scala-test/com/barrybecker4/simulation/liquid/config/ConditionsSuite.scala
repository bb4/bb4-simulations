// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import org.scalatest.funsuite.AnyFunSuite

/**
  * Regression tests for JSON config parsing.
  */
class ConditionsSuite extends AnyFunSuite {

  test("BasicConfigParsesWallSourceAndRegion") {
    val c = new Conditions(ConfigurationEnum.BASIC.fileName)
    assertResult(26) { c.getGridWidth }
    assertResult(21) { c.getGridHeight }
    assertResult(10.0) { c.getCellSize }
    assertResult(-9.8) { c.getGravity }

    assertResult(1) { c.getWalls.size }
    val w = c.getWalls.head
    assertResult(15.0) { w.getStartPoint.x }
    assertResult(10.0) { w.getStartPoint.y }
    assertResult(15.0) { w.getStopPoint.x }
    assertResult(15.0) { w.getStopPoint.y }

    assertResult(1) { c.getSources.size }
    val s = c.getSources.head
    assertResult(5) { s.getStart.getX }
    assertResult(6) { s.getStart.getY }
    assertResult(20.0) { s.getVelocity.x }
    assertResult(0.0) { s.getVelocity.y }

    assertResult(1) { c.getInitialLiquidRegions.size }
    val r = c.getInitialLiquidRegions.head
    assertResult(1) { r.getStart.getX }
    assertResult(18) { r.getStart.getY }
    assertResult(25) { r.getStop.getX }
    assertResult(21) { r.getStop.getY }
  }

  test("PulseLargeAppliesSourceTiming") {
    val c = new Conditions(ConfigurationEnum.PULSE_LARGE.fileName)
    assertResult(1) { c.getSources.size }
    val s = c.getSources.head
    assertResult(0.5) { s.startTime }
    assertResult(1.0) { s.duration }
    assertResult(3.0) { s.repeatInterval }
  }
}
