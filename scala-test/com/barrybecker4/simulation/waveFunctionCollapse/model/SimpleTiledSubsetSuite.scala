// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.getSampleTiledData
import org.scalatest.funsuite.AnyFunSuite

class SimpleTiledSubsetSuite extends AnyFunSuite {

  test("resolveSubsetTileNames null uses all-tiles path") {
    val data = getSampleTiledData("Circuit").set
    assert(SimpleTiledModel.resolveSubsetTileNames(null, data) == null)
  }

  test("resolveSubsetTileNames Debug subset is smaller than Turnless") {
    val data = getSampleTiledData("Circuit").set
    val debug = SimpleTiledModel.resolveSubsetTileNames("Debug", data)
    val turnless = SimpleTiledModel.resolveSubsetTileNames("Turnless", data)
    assert(debug.size == 2)
    assert(debug.toSet == Set("substrate", "turn"))
    assert(turnless.size > debug.size)
    assert(turnless.toSet.contains("bridge"))
  }

  test("resolveSubsetTileNames throws for unknown subset") {
    val data = getSampleTiledData("Circuit").set
    intercept[IllegalArgumentException] {
      SimpleTiledModel.resolveSubsetTileNames("NoSuchSubset", data)
    }
  }
}
