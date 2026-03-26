// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.simulation.habitat.creatures.Neighbors.{distanceTo, getDirectionTo}
import org.scalatest.funsuite.AnyFunSuite

import javax.vecmath.Point2d

class NeighborsSuite extends AnyFunSuite {

  private val eps = 1e-12

  test("Distance when within the same global bounds") {
    val pairs = Seq(
      (new Point2d(0.1, 0.1), new Point2d(0.2, 0.1)),
      (new Point2d(0.2, 0.1), new Point2d(0.2, 0.9)),
      (new Point2d(0.5, 0.5), new Point2d(0.4, 0.5)),
      (new Point2d(0.0, 0.1), new Point2d(0.3, 0.5))
    )
    val expected = Seq(0.1, 0.2, 0.1, 0.5)
    pairs.zip(expected).foreach { case ((a, b), d) =>
      assert(math.abs(distanceTo(a, b) - d) < eps)
    }
  }

  test("Distance when one point outside the global [0,0] - [1,1] region (it should wrap)") {
    val pairs = Seq(
      (new Point2d(0.1, 0.1), new Point2d(0.2, 1.2)),
      (new Point2d(0.1, 0.1), new Point2d(0.1, 1.2)),
      (new Point2d(-0.1, 0.1), new Point2d(0.2, 1.5))
    )
    val expected = Seq(0.14142135623730948, 0.1, 0.5)
    pairs.zip(expected).foreach { case ((a, b), d) =>
      assert(math.abs(distanceTo(a, b) - d) < eps)
    }
  }

  test("direction to prey when within global bounds") {
    val pairs = Seq(
      (new Point2d(0.1, 0.1), new Point2d(0.2, 0.1)),
      (new Point2d(0.1, 0.1), new Point2d(0.1, 0.3)),
      (new Point2d(0.4, 0.2), new Point2d(0.2, 0.2)),
      (new Point2d(0.2, 0.2), new Point2d(0.2, 0.19)),
      (new Point2d(0.2, 0.1), new Point2d(0.2, 0.9)),
      (new Point2d(0.7, 0.5), new Point2d(0.1, 0.5)),
      (new Point2d(0.0, 0.1), new Point2d(0.3, 0.5))
    )
    val expected = Seq(0.0, math.Pi / 2, math.Pi, -math.Pi / 2, math.Pi / 2, 0.0, 0.9272952180016121)
    pairs.zip(expected).foreach { case ((a, b), ang) =>
      assert(math.abs(getDirectionTo(a, b) - ang) < eps)
    }
  }

  test("direction to prey when one is outside of global bounds") {
    val pairs = Seq(
      (new Point2d(0.1, 0.1), new Point2d(0.2, 1.2)),
      (new Point2d(0.1, 0.1), new Point2d(0.1, 1.2)),
      (new Point2d(-0.1, 0.1), new Point2d(0.2, 1.5))
    )
    val expected = Seq(0.7853981633974494, math.Pi / 2, 0.9272952180016122)
    pairs.zip(expected).foreach { case ((a, b), ang) =>
      assert(math.abs(getDirectionTo(a, b) - ang) < eps)
    }
  }
}
