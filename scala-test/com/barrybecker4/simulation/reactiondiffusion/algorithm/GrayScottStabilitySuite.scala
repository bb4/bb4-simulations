/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

class GrayScottStabilitySuite extends AnyFunSuite {

  test("maxStableDt scales with h squared") {
    val h1 = GrayScottAlgorithm.maxStableDt(1.0)
    val h2 = GrayScottAlgorithm.maxStableDt(2.0)
    assert(math.abs(h2 / h1 - 4.0) < 1e-12)
  }

  test("timeStep clamps oversized dt") {
    val c = new GrayScottController(8, 8)
    c.reset()
    c.setParallelized(false)
    c.setH(1.0)
    val maxDt = GrayScottAlgorithm.maxStableDt(1.0)
    // Request far above stability limit; step should still stay finite
    c.timeStep(maxDt * 10.0)
    val m = c.model
    for (x <- 0 until m.getWidth; y <- 0 until m.getHeight) {
      assert(!m.u(x, y).isNaN && !m.u(x, y).isInfinite)
      assert(!m.v(x, y).isNaN && !m.v(x, y).isInfinite)
    }
  }
}
