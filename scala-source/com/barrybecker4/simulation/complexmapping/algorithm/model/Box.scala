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

  /** Grow the axis-aligned bounds so they include `pt` (y increases upward). */
  def extendBy(pt: Point2d): Box =
    Box(
      new Point2d(Math.min(upperLeft.x, pt.x), Math.max(upperLeft.y, pt.y)),
      new Point2d(Math.max(lowerRight.x, pt.x), Math.min(lowerRight.y, pt.y))
    )

  def addMargin(margin: Double): Box = {
    Box(
      new Point2d(upperLeft.x - margin, upperLeft.y + margin),
      new Point2d(lowerRight.x + margin, lowerRight.y - margin)
    )
  }
}
