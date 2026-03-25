/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.model

import com.barrybecker4.simulation.complexmapping.algorithm.functions.{IdentityFunction, IntPowerFunction}
import javax.vecmath.Point2d
import org.scalatest.funsuite.AnyFunSuite

class GridSuite extends AnyFunSuite {

  private val bounds = Box(new Point2d(-1.0, 1.0), new Point2d(1.0, -1.0))

  test("transform with interpolation 0 returns same grid instance") {
    val grid = new Grid(bounds, 0.5, 0.5)
    val out = grid.transform(IdentityFunction(), 1, 0.0)
    assert(out eq grid)
  }

  test("transform with interpolation 1 applies full function") {
    val mesh = Array(Array(new MeshPoint(2.0, 0.0, 0.5)))
    val grid = Grid(mesh)
    val out = grid.transform(IntPowerFunction(), 2, 1.0)
    assert(out(0, 0).x == 4.0)
    assert(out(0, 0).y == 0.0)
  }

  test("transform with interpolation 0.5 blends original and image") {
    val mesh = Array(Array(new MeshPoint(2.0, 0.0, 0.5)))
    val grid = Grid(mesh)
    val out = grid.transform(IntPowerFunction(), 2, 0.5)
    assert(out(0, 0).x == 3.0)
    assert(out(0, 0).y == 0.0)
  }

  test("transform with default interpolation 1 matches explicit 1.0") {
    val grid = new Grid(bounds, 1.0, 1.0)
    val a = grid.transform(IdentityFunction(), 1)
    val b = grid.transform(IdentityFunction(), 1, 1.0)
    assert(a.width == b.width && a.height == b.height)
    for (i <- 0 until a.width; j <- 0 until a.height) {
      assert(a(i, j).x == b(i, j).x)
      assert(a(i, j).y == b(i, j).y)
    }
  }
}
