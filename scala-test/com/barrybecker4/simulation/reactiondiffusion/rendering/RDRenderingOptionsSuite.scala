/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import org.scalatest.funsuite.AnyFunSuite

class RDRenderingOptionsSuite extends AnyFunSuite {

  test("default parallel rendering off") {
    val opts = new RDRenderingOptions()
    assert(!opts.isParallelized)
  }
}
