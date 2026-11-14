/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

/**
  * Resize is deferred until timeStep; applying it must keep model dimensions
  * consistent with concentration fields so rendering cannot AIOOBE.
  */
class GrayScottControllerSuite extends AnyFunSuite {

  test("deferred setSize applied on timeStep keeps arrays in sync with dimensions") {
    val controller = new GrayScottController(10, 10)
    controller.setSize(20, 15)
    // Not applied yet
    assert(controller.model.getWidth == 10)

    controller.timeStep(1.0)

    val m = controller.model
    assert(m.getWidth == 20)
    assert(m.getHeight == 15)
    val _ = m.u(m.getWidth - 1, m.getHeight - 1) + m.v(m.getWidth - 1, m.getHeight - 1)
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

    parallel.model.copyConcentrationsFrom(sequential.model)

    val dt = 0.05
    for (_ <- 0 until 5) {
      sequential.timeStep(dt)
      parallel.timeStep(dt)
    }

    val maxDiff = maxAbsDiff(sequential.model, parallel.model)
    assert(maxDiff < 1e-9, s"max abs diff was $maxDiff")
  }

  test("edge cells are updated under fully periodic step") {
    val c = new GrayScottController(8, 8)
    c.reset()
    c.setParallelized(false)
    // Distinct edge value so a step must change it if edges are included.
    // After commit, the newly written state is in tmp* (display lags one swap).
    c.model.initializePoint(0, 0, 0.7, 0.2)
    c.timeStep(0.05)
    assert(c.model.tmpU(0, 0) != 0.7, s"tmpU remained ${c.model.tmpU(0, 0)}")
  }

  private def maxAbsDiff(a: GrayScottModel, b: GrayScottModel): Double = {
    var max = 0.0
    for (x <- 0 until a.getWidth; y <- 0 until a.getHeight) {
      max = math.max(max, math.abs(a.u(x, y) - b.u(x, y)))
      max = math.max(max, math.abs(a.v(x, y) - b.v(x, y)))
    }
    max
  }
}
