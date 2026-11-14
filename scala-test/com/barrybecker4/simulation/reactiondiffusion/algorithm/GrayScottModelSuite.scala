/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

class GrayScottModelSuite extends AnyFunSuite {

  test("periodic mid") {
    assertResult(10) { GrayScottModel.getPeriodicXValue(10, 100) }
  }

  test("periodic low") {
    assertResult(93) { GrayScottModel.getPeriodicXValue(-7, 100) }
  }

  test("periodic high") {
    assertResult(7) { GrayScottModel.getPeriodicXValue(107, 100) }
  }

  test("periodic zero and max minus one") {
    assertResult(0) { GrayScottModel.getPeriodicXValue(0, 100) }
    assertResult(99) { GrayScottModel.getPeriodicXValue(99, 100) }
    assertResult(99) { GrayScottModel.getPeriodicXValue(-1, 100) }
  }

  test("periodic max equals period wraps to zero") {
    assertResult(0) { GrayScottModel.getPeriodicXValue(100, 100) }
  }

  test("periodic with max one") {
    assertResult(0) { GrayScottModel.getPeriodicXValue(0, 1) }
    assertResult(0) { GrayScottModel.getPeriodicXValue(1, 1) }
    assertResult(0) { GrayScottModel.getPeriodicXValue(-1, 1) }
  }

  test("commitChanges swaps u/tmpU and v/tmpV field references") {
    val m = new GrayScottModel(5, 5)
    val uBefore = m.uWrite
    val tmpUBefore = m.tmpU
    val vBefore = m.vWrite
    val tmpVBefore = m.tmpV
    m.commitChanges()
    assert(m.uWrite eq tmpUBefore)
    assert(m.tmpU eq uBefore)
    assert(m.vWrite eq tmpVBefore)
    assert(m.tmpV eq vBefore)
  }

  test("initializePoint sets u, v, tmpU, tmpV for in-bounds") {
    val m = new GrayScottModel(5, 5)
    m.initializePoint(2, 3, 0.123, 0.456)
    assert(m.u(2, 3) == 0.123)
    assert(m.v(2, 3) == 0.456)
    assert(m.tmpU(2, 3) == 0.123)
    assert(m.tmpV(2, 3) == 0.456)
  }

  test("initializePoint ignores out-of-bounds") {
    val m = new GrayScottModel(5, 5)
    val before = m.u(2, 2)
    m.initializePoint(5, 2, 99, 99)
    m.initializePoint(2, -1, 99, 99)
    assert(m.u(2, 2) == before)
  }

  test("setSize keeps width/height consistent and corner access works") {
    val m = new GrayScottModel(10, 10)
    m.setF(0.03)
    m.setK(0.06)
    m.setSize(new java.awt.Dimension(20, 15))
    assert(m.getWidth == 20)
    assert(m.getHeight == 15)
    val _ = m.u(m.getWidth - 1, m.getHeight - 1) + m.v(m.getWidth - 1, m.getHeight - 1)
    // size-only reallocate preserves F/K
    assert(m.getF == 0.03)
    assert(m.getK == 0.06)
  }

  test("resetState restores default F and K") {
    val m = new GrayScottModel(8, 8)
    m.setF(0.03)
    m.setK(0.06)
    m.resetState()
    assert(m.getF == GrayScottModel.F0)
    assert(m.getK == GrayScottModel.K0)
  }
}
