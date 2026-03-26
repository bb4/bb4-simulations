// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fractalexplorer

import org.scalatest.funsuite.AnyFunSuite

class ZoomBoxSuite extends AnyFunSuite {

  test("isValidBox requires two distinct corners") {
    val z = new ZoomBox()
    assert(!z.isValidBox)
    z.setFirstCorner(0, 0)
    assert(!z.isValidBox)
    z.setSecondCorner(0, 0)
    assert(!z.isValidBox)
    z.setSecondCorner(10, 10)
    assert(z.isValidBox)
  }

  test("clearBox clears validity") {
    val z = new ZoomBox()
    z.setFirstCorner(0, 0)
    z.setSecondCorner(20, 20)
    z.finalizeBoxForRange(1.0, keepAspectRatio = false)
    z.clearBox()
    assert(!z.isValidBox)
  }
}
