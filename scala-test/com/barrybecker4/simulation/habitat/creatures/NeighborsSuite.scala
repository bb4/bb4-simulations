// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import com.barrybecker4.simulation.habitat.creatures.Neighbors.{distanceTo, getDirectionTo}
import com.barrybecker4.simulation.liquid.config.SourceSuite.test
import org.scalatest.funsuite.AnyFunSuite

import javax.vecmath.Point2d


class NeighborsSuite extends AnyFunSuite {

  test("Distance when within the same global bounds") {

    val pairs = Seq(
      (new Point2d(0.1, 0.1), new Point2d(0.2, 0.1)),
      (new Point2d(0.2, 0.1), new Point2d(0.2, 0.9)),
      (new Point2d(0.5, 0.5), new Point2d(0.4, 0.5)),
      (new Point2d(0.0, 0.1), new Point2d(0.3, 0.5))
    )
    assertResult("0.09999999999999987, 0.20000000000000018, 0.10000000000000009, 0.49999999999999994") {
      pairs.map(pair => distanceTo(pair._1, pair._2)).mkString(", ")
    }
  }

  test("Distance when one point outside the global [0,0] - [1,1] region (it should wrap)") {

    val pairs = Seq(
      (new Point2d(0.1, 0.1), new Point2d(0.2, 1.2)),
      (new Point2d(0.1, 0.1), new Point2d(0.1, 1.2)),
      (new Point2d(-0.1, 0.1), new Point2d(0.2, 1.5))
    )
    assertResult("0.14142135623730948, 0.10000000000000009, 0.4999999999999999") {
      pairs.map(pair => distanceTo(pair._1, pair._2)).mkString(", ")
    }
  }

  test("direction to prey when within global bounds") {
    val pairs = Seq(
      (new Point2d(0.1, 0.1), new Point2d(0.2, 0.1)), // to the right, 0 degrees
      (new Point2d(0.1, 0.1), new Point2d(0.1, 0.3)), // to the north, 90 degrees
      (new Point2d(0.4, 0.2), new Point2d(0.2, 0.2)), // to the left, 180 degrees
      (new Point2d(0.2, 0.2), new Point2d(0.2, 0.19)), // to the south, 270 degrees
      (new Point2d(0.2, 0.1), new Point2d(0.2, 0.9)), // to the south, 270
      (new Point2d(0.7, 0.5), new Point2d(0.1, 0.5)),
      (new Point2d(0.0, 0.1), new Point2d(0.3, 0.5))
    )
    assertResult("0.0, 1.5707963267948966, 3.141592653589793, -1.5707963267948966, 1.5707963267948966, 0.0, 0.9272952180016121") {
      pairs.map(pair => getDirectionTo(pair._1, pair._2)).mkString(", ")
    }
  }

  test("direction to prey when one is outside of global bounds") {
    val pairs = Seq(
      (new Point2d(0.1, 0.1), new Point2d(0.2, 1.2)),
      (new Point2d(0.1, 0.1), new Point2d(0.1, 1.2)),
      (new Point2d(-0.1, 0.1), new Point2d(0.2, 1.5))
    )
    assertResult("0.7853981633974494, 1.5707963267948966, 0.9272952180016122") {
      pairs.map(pair => getDirectionTo(pair._1, pair._2)).mkString(", ")
    }
  }
}
