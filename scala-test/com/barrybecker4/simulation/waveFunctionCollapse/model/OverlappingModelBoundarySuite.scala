// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import org.scalatest.funsuite.AnyFunSuite

/**
  * Regression: solver coordinates must be grid cells, not pixel size.
  * With scale=2, a 48x48 pixel request yields a 24x24 wave grid.
  */
class OverlappingModelBoundarySuite extends AnyFunSuite {

  private def skyline(scale: Int, periodic: Boolean, ground: Int = 0): OverlappingModel =
    new OverlappingModel(
      name = "Skyline",
      width = 48,
      height = 48,
      periodicOutput = periodic,
      imageParams = OverlappingImageParams(N = 3, symmetry = 1, periodicInput = true, groundParam = ground),
      limit = 0,
      allowInconsistencies = false,
      scale = scale
    )

  test("non-periodic onBoundary uses grid size when scale=2") {
    val model = skyline(scale = 2, periodic = false)
    val N = 3
    val gridW = 24
    val gridH = 24
    assert(model.onBoundary(gridW - N + 1, 0))
    assert(model.onBoundary(0, gridH - N + 1))
    // Same absolute coords were wrongly treated as interior when FMX was pixel 48.
    assert(model.onBoundary(22, 0))
    assert(!model.onBoundary(0, 0))
    assert(!model.onBoundary(gridW - N, gridH - N))
  }

  test("ground bans with scale=2 land inside the wave") {
    // groundParam -1 → ground index (tCounter - 1) % tCounter; non-zero for Skyline.
    val model = skyline(scale = 2, periodic = true, ground = -1)
    model.startRun(99)
    val gridW = 24
    val gridH = 24
    val bottom = (gridH - 1) * gridW
    assert(model.waveSize == gridW * gridH)
    // Bottom row forced to ground only → domain size 1 after clear+propagate.
    assert(model.cellSumOfOnes(bottom) == 1)
    assert(model.cellSumOfOnes(0) < model.tileCount)
  }
}
