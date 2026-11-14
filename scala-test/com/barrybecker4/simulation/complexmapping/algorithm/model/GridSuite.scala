/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.model

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.simulation.complexmapping.algorithm.functions.{ComplexFunction, IdentityFunction, IntPowerFunction}
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

  test("partial interpolation evaluates the function once per point") {
    var calls = 0
    val countingFn = new ComplexFunction {
      override def compute(s: ComplexNumber, n: Int): ComplexNumber = {
        calls += 1
        ComplexNumber(s.real * 2.0, s.imaginary)
      }
    }
    val mesh = Array(Array(new MeshPoint(2.0, 0.0, 0.5)))
    val grid = Grid(mesh)
    val out = grid.transform(countingFn, 1, 0.5)
    assert(calls == 1)
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

  test("degenerate bounds produce empty mesh with height 0") {
    val tiny = Box(new Point2d(0.0, 0.0), new Point2d(0.0, 0.0))
    val grid = new Grid(tiny, 0.5, 0.5)
    assert(grid.width == 0)
    assert(grid.height == 0)
  }

  test("increment larger than bounds produces empty mesh") {
    val box = Box(new Point2d(0.0, 1.0), new Point2d(0.4, 0.0))
    val grid = new Grid(box, 1.0, 1.0)
    assert(grid.width == 0)
    assert(grid.height == 0)
  }
}
