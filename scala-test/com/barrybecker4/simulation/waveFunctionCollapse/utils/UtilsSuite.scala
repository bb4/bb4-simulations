// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.utils

import org.scalatest.funsuite.AnyFunSuite

class UtilsSuite extends AnyFunSuite {

  test("randomFromArray picks heavier index when r is low") {
    val w = Array(0.9, 0.1)
    assert(Utils.randomFromArray(w.clone(), 0.0) == 0)
  }

  test("randomFromArray picks second index when r is high") {
    val w = Array(0.5, 0.5)
    assert(Utils.randomFromArray(w.clone(), 0.99) == 1)
  }

  test("randomFromArray all-zero weights becomes uniform") {
    val w = Array(0.0, 0.0, 0.0)
    val idx = Utils.randomFromArray(w, 0.5)
    assert(idx >= 0 && idx < 3)
  }
}
