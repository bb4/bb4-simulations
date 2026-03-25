/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.model

import com.barrybecker4.simulation.complexmapping.algorithm.functions.IdentityFunction
import javax.vecmath.Point2d
import org.scalatest.funsuite.AnyFunSuite

class MeshMappingModelSuite extends AnyFunSuite {

  private val bounds = Box(new Point2d(-1.0, 1.0), new Point2d(1.0, -1.0))
  private val grid = new Grid(bounds, 0.5, 0.5)

  test("getImage invalidates cache when pixel width or height changes") {
    val model =
      MeshMappingModel(grid = grid, function = IdentityFunction(), n = 1, interpolationVal = 1.0)
    val small = model.getImage(48, 36)
    assert(small.getWidth == 48)
    assert(small.getHeight == 36)
    val large = model.getImage(96, 72)
    assert(large.getWidth == 96)
    assert(large.getHeight == 72)
  }

  test("getImage auto viewport switches invalidate fixed viewport cache") {
    val model =
      MeshMappingModel(grid = grid, function = IdentityFunction(), n = 1, interpolationVal = 1.0)
    val viewport = Box(new Point2d(-2, 2), new Point2d(2, -2))
    val fixed = model.getImage(viewport, 40, 40)
    assert(fixed.getWidth == 40)
    val auto = model.getImage(40, 40)
    assert(auto.getWidth == 40)
    assert(auto.getHeight == 40)
  }
}
