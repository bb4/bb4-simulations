/*
 * Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.simulation.complexmapping.algorithm.model

import javax.vecmath.Point2d

case class Box(upperLeft: Point2d, lowerRight: Point2d) {
  def leftX: Double = upperLeft.x
  def rightX: Double = lowerRight.x
  def topY: Double = upperLeft.y
  def bottomY: Double = lowerRight.y
  def width: Double = rightX - leftX
  def height: Double = topY - bottomY

  def extendBy(pt: Point2d): Box = {
    if (pt.x < upperLeft.x || pt.y > upperLeft.y) {
      Box(new Point2d(Math.min(pt.x, upperLeft.x), Math.max(pt.y, upperLeft.y)), lowerRight)
    }
    else if (pt.x > lowerRight.x || pt.y < lowerRight.y) {
      Box(upperLeft, new Point2d(Math.max(pt.x, lowerRight.x), Math.min(pt.y, lowerRight.y)))
    } else this
  }

  def addMargin(margin: Double): Box = {
    Box(
      new Point2d(upperLeft.x - margin, upperLeft.y + margin),
      new Point2d(lowerRight.x + margin, lowerRight.y - margin)
    )
  }
}
