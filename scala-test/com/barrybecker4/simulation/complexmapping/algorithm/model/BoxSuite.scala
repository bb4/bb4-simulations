/*
 * Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.simulation.complexmapping.algorithm.model

import javax.vecmath.Point2d
import org.scalatest.funsuite.AnyFunSuite

class BoxSuite extends AnyFunSuite {

  test("extendBy includes a point outside on both axes in one call") {
    val b = Box(new Point2d(-1, 1.5), new Point2d(2, -2))
    val extended = b.extendBy(new Point2d(-10, -10))
    assert(extended.leftX == -10)
    assert(extended.rightX == 2)
    assert(extended.topY == 1.5)
    assert(extended.bottomY == -10)
  }

  test("extendBy leaves box unchanged when point is inside") {
    val b = Box(new Point2d(-1, 1.5), new Point2d(2, -2))
    val extended = b.extendBy(new Point2d(0, 0))
    assert(extended.upperLeft.x == b.upperLeft.x)
    assert(extended.upperLeft.y == b.upperLeft.y)
    assert(extended.lowerRight.x == b.lowerRight.x)
    assert(extended.lowerRight.y == b.lowerRight.y)
  }

  test("extendBy from degenerate origin box grows to first point") {
    val origin = Box(new Point2d(0, 0), new Point2d(0, 0))
    val b = origin.extendBy(new Point2d(3, -4))
    assert(b.leftX == 0 && b.rightX == 3)
    assert(b.topY == 0 && b.bottomY == -4)
  }

  test("chained extendBy matches cumulative min/max of all points") {
    val b0 = Box(new Point2d(0, 0), new Point2d(0, 0))
    val points = Seq(
      new Point2d(1, 2),
      new Point2d(-5, -3),
      new Point2d(7, 0)
    )
    val chained = points.foldLeft(b0)(_.extendBy(_))
    assert(chained.leftX == -5)
    assert(chained.rightX == 7)
    assert(chained.topY == 2)
    assert(chained.bottomY == -3)
  }
}
