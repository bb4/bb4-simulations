// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import org.scalatest.funsuite.AnyFunSuite

class SimpleTiledBoundarySuite extends AnyFunSuite {

  test("non-periodic onBoundary uses tile grid not pixel size") {
    // Knots tilesize is 10; 40x40 → 4x4 grid.
    val model = new SimpleTiledModel(
      width = 40,
      height = 40,
      name = "Knots",
      subsetName = null,
      isPeriodic = false,
      black = false,
      limit = 0,
      allowInconsistencies = false
    )
    assert(model.onBoundary(-1, 0))
    assert(model.onBoundary(0, -1))
    assert(!model.onBoundary(0, 0))
    assert(model.onBoundary(4, 0), "x=4 is outside a 4-wide grid")
    assert(!model.onBoundary(3, 3))

    model.startRun(7)
    assert(model.waveSize == 4 * 4)
  }

  test("textOutput indexes observed tiles on the grid after collapse") {
    val model = new SimpleTiledModel(
      width = 20,
      height = 20,
      name = "Knots",
      subsetName = null,
      isPeriodic = true,
      black = false,
      limit = 0,
      allowInconsistencies = false
    )
    var ok = false
    var seed = 100
    while (!ok && seed < 130) {
      ok = model.runWithLimit(seed)
      seed += 1
    }
    assume(ok, "could not collapse Knots in seed range — skip textOutput check")
    val text = model.textOutput()
    assert(text.nonEmpty)
    assert(text.linesIterator.length == model.waveSize)
  }
}
