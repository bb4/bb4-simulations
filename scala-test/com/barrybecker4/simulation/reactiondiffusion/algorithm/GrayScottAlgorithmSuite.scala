/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

class GrayScottAlgorithmSuite extends AnyFunSuite {

  test("one sequential time step yields finite bounded concentrations") {
    val c = new GrayScottController(5, 5)
    c.reset()
    c.setParallelized(false)
    c.timeStep(0.05)
    val m = c.getModel
    for (x <- 0 until m.getWidth; y <- 0 until m.getHeight) {
      assert(!m.u(x)(y).isNaN && !m.u(x)(y).isInfinite)
      assert(!m.v(x)(y).isNaN && !m.v(x)(y).isInfinite)
      assert(m.u(x)(y) >= -0.5 && m.u(x)(y) <= 2.0)
      assert(m.v(x)(y) >= -0.5 && m.v(x)(y) <= 2.0)
    }
  }
}
