package com.barrybecker4.simulation.water.ui

import org.scalatest.funsuite.AnyFunSuite

class InteractionMathSuite extends AnyFunSuite {

  test("horizontalRange clamps to width and is centered on xpos") {
    assert(InteractionMath.horizontalRange(5, 1, 20) == (0, 15))
    assert(InteractionMath.horizontalRange(5, 0, 20) == (5, 5))
    assert(InteractionMath.horizontalRange(0, 2, 5) == (0, 4))
  }

  test("heightDeltaGaussian uses gauss callback at scaled offset") {
    val d = InteractionMath.heightDeltaGaussian(10.0, 4.0, t => t) // identity
    assert(math.abs(d - 0.3 * 10.0 * (1.0 - 0.5 * 4.0 / 10.0)) < 1e-10)
  }
}
