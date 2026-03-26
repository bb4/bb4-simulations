// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase.algorithm

import org.scalatest.funsuite.AnyFunSuite

class HenonAlgorithmSuite extends AnyFunSuite {

  test("after reset image exists and matches default model dimensions") {
    val a = new HenonAlgorithm()
    val img = a.getImage
    assert(img != null)
    assert(img.getWidth == 300)
    assert(img.getHeight == 300)
  }

  test("timeStep advances without error and image remains valid") {
    val a = new HenonAlgorithm()
    a.setMaxIterations(50)
    a.setStepsPerFrame(10)
    var done = false
    var steps = 0
    while (!done && steps < 20) {
      done = a.timeStep()
      steps += 1
      val img = a.getImage
      assert(img != null)
      assert(img.getWidth == 300)
      assert(img.getHeight == 300)
    }
    assert(done)
  }
}
