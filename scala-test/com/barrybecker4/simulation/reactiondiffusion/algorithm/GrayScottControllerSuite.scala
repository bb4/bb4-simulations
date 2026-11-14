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

  test("new controller initializes H to H0") {
    val controller = new GrayScottController(5, 5)
    assert(controller.algorithm.getH == GrayScottModel.H0)
  }

  test("deferred resize preserves non-default H") {
    val controller = new GrayScottController(10, 10)
    controller.setH(0.5)
    assert(controller.algorithm.getH == 0.5)

    controller.setSize(20, 15)
    controller.timeStep(0.05)

    assert(controller.algorithm.getH == 0.5)
  }

  test("parallel and sequential timeStep match within epsilon") {
    val sequential = new GrayScottController(24, 24)
    val parallel = new GrayScottController(24, 24)
    sequential.reset()
    parallel.reset()
    sequential.setParallelized(false)
    parallel.setParallelized(true)

    copyState(sequential.model, parallel.model)

    val dt = 0.05
    for (_ <- 0 until 5) {
      sequential.timeStep(dt)
      parallel.timeStep(dt)
    }

    val maxDiff = maxAbsDiff(sequential.model, parallel.model)
    assert(maxDiff < 1e-9, s"max abs diff was $maxDiff")
  }

  private def copyState(from: GrayScottModel, to: GrayScottModel): Unit = {
    require(from.getWidth == to.getWidth && from.getHeight == to.getHeight)
    for (x <- 0 until from.getWidth; y <- 0 until from.getHeight) {
      to.u(x)(y) = from.u(x)(y)
      to.v(x)(y) = from.v(x)(y)
      to.tmpU(x)(y) = from.tmpU(x)(y)
      to.tmpV(x)(y) = from.tmpV(x)(y)
    }
  }

  private def maxAbsDiff(a: GrayScottModel, b: GrayScottModel): Double = {
    var max = 0.0
    for (x <- 0 until a.getWidth; y <- 0 until a.getHeight) {
      max = math.max(max, math.abs(a.u(x)(y) - b.u(x)(y)))
      max = math.max(max, math.abs(a.v(x)(y) - b.v(x)(y)))
    }
    max
  }
}
