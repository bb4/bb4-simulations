/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

/**
  * Resize is deferred until timeStep; applying it must keep model dimensions
  * consistent with u/v so rendering cannot AIOOBE.
  */
class GrayScottControllerSuite extends AnyFunSuite {

  test("deferred setSize applied on timeStep keeps arrays in sync with dimensions") {
    val controller = new GrayScottController(10, 10)
    controller.setSize(20, 15)
    // Not applied yet
    assert(controller.model.getWidth == 10)
    assert(controller.model.u.length == 10)

    controller.timeStep(1.0)

    val m = controller.model
    assert(m.getWidth == 20)
    assert(m.getHeight == 15)
    assert(m.u.length == m.getWidth)
    assert(m.u(0).length == m.getHeight)
    assert(m.v.length == m.getWidth)
    assert(m.v(0).length == m.getHeight)
    val _ = m.u(m.getWidth - 1)(m.getHeight - 1) + m.v(m.getWidth - 1)(m.getHeight - 1)
  }
}
