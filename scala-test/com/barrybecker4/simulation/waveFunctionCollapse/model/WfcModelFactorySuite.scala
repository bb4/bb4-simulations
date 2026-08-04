// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{Overlapping, SimpleTiled}
import org.scalatest.funsuite.AnyFunSuite

class WfcModelFactorySuite extends AnyFunSuite {

  test("overlapping factory creates OverlappingModel with requested name and limit") {
    val sample = Overlapping(
      n = "2",
      ground = "0",
      height = "48",
      limit = "0",
      name = "Flowers",
      periodic = "True",
      periodicInput = "True",
      screenshots = "1",
      symmetry = "8",
      width = "48"
    )
    val image = OverlappingImageParams(N = 2, symmetry = 8, periodicInput = true, groundParam = 0)
    val model = WfcModelFactory.overlapping(
      sample, 48, 48, periodicOutput = true, image, allowInconsistencies = true, limit = 10
    )
    assert(model.isInstanceOf[OverlappingModel])
    assert(model.getName == "Flowers")
    assert(model.asInstanceOf[OverlappingModel].limit == 10)
  }

  test("simpleTiled factory creates SimpleTiledModel with requested name") {
    val sample = SimpleTiled(
      width = "20",
      height = "20",
      black = "False",
      limit = "0",
      name = "Summer",
      periodic = "True",
      screenshots = "1",
      subset = null
    )
    val model = WfcModelFactory.simpleTiled(
      sample, 20, 20, subset = null, periodicOutput = true, black = false,
      allowInconsistencies = true, limit = 5
    )
    assert(model.isInstanceOf[SimpleTiledModel])
    assert(model.getName == "Summer")
  }
}
