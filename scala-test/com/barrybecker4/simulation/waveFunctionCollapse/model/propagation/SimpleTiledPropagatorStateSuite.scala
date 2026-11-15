// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.propagation

import com.barrybecker4.simulation.waveFunctionCollapse.model.SimpleTiledModel
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.getSampleTiledData
import org.scalatest.funsuite.AnyFunSuite

class SimpleTiledPropagatorStateSuite extends AnyFunSuite {

  test("Circuit Debug subset builds a model with reduced tile count") {
    val all = new SimpleTiledModel(
      width = 28,
      height = 28,
      name = "Circuit",
      subsetName = null,
      isPeriodic = true,
      black = false
    )
    val debug = new SimpleTiledModel(
      width = 28,
      height = 28,
      name = "Circuit",
      subsetName = "Debug",
      isPeriodic = true,
      black = false
    )
    assert(debug.tileCount < all.tileCount)
    assert(debug.tileCount >= 2)
  }

  test("Debug subset neighbor rules reference only subset base tiles") {
    val data = getSampleTiledData("Circuit")
    val subset = SimpleTiledModel.resolveSubsetTileNames("Debug", data.set).get.toSet
    assert(subset == Set("substrate", "turn"))
    val neighbors = data.set.neighbors
    assert(neighbors != null && neighbors.nonEmpty)
    val relevant = neighbors.filter { n =>
      val left = n.left.split(" ").head
      val right = n.right.split(" ").head
      subset.contains(left) && subset.contains(right)
    }
    assert(relevant.nonEmpty, "expected at least one neighbor rule within Debug subset")
  }
}
