/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm

import javax.vecmath.Point2d

case class Box(upperLeft: Point2d, lowerRight: Point2d) {
  def leftX: Double = upperLeft.x
  def rightX: Double = lowerRight.x
  def topY: Double = upperLeft.y
  def bottomY: Double = lowerRight.y
  def width: Double = leftX - rightX
  def height: Double = topY - bottomY
}
