/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author Barry
  */
class GrayScottModelSuite extends AnyFunSuite {


  test("periodic mid") {
    assertResult(10 ) { GrayScottModel.getPeriodicXValue(10, 100) }
  }

  test("periodic low") {
    assertResult(93 ) { GrayScottModel.getPeriodicXValue(-7, 100) }
  }

  test("periodic high") {
    assertResult(7) { GrayScottModel.getPeriodicXValue(107, 100) }
  }

  test("periodic zero and max minus one") {
    assertResult(0) { GrayScottModel.getPeriodicXValue(0, 100) }
    assertResult(99) { GrayScottModel.getPeriodicXValue(99, 100) }
    assertResult(99) { GrayScottModel.getPeriodicXValue(-1, 100) }
  }

  test("commitChanges swaps u/tmpU and v/tmpV") {
    val m = new GrayScottModel(5, 5)
    val uBefore = m.u
    val tmpUBefore = m.tmpU
    val vBefore = m.v
    val tmpVBefore = m.tmpV
    m.commitChanges()
    assert(m.u eq tmpUBefore)
    assert(m.tmpU eq uBefore)
    assert(m.v eq tmpVBefore)
    assert(m.tmpV eq vBefore)
  }

  test("initializePoint sets u, v, tmpU, tmpV for in-bounds") {
    val m = new GrayScottModel(5, 5)
    m.initializePoint(2, 3, 0.123, 0.456)
    assert(m.u(2)(3) == 0.123)
    assert(m.v(2)(3) == 0.456)
    assert(m.tmpU(2)(3) == 0.123)
    assert(m.tmpV(2)(3) == 0.456)
  }

  test("initializePoint ignores out-of-bounds") {
    val m = new GrayScottModel(5, 5)
    val before = m.u(2)(2)
    m.initializePoint(5, 2, 99, 99)
    m.initializePoint(2, -1, 99, 99)
    assert(m.u(2)(2) == before)
  }
}
