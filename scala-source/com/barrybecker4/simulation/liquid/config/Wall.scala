// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import com.barrybecker4.common.geometry.Location
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D


/**
  * Walls form a basis for solid objects in the simulation space.
  * They are straight lines  (either horizontal or vertical) and immovable.
  * Endpoints of walls must be on cell boundaries
  * @author Barry Becker
  */
class Wall private(val x1: Double, val y1: Double, val x2: Double, val y2: Double) {

  assert(x1 == x2 || y1 == y2)
  final private var segment = new Line2D.Double(x1, y1, x2, y2) // wall endpoints
  final private var thickness = 2.0f // wall thickness

  /**
    * The start and stop locations define the endpoints of a horizontal or vertical line.
    * @param startLocation begin point for wall
    * @param stopLocation  endpoint for wall
    */
  def this(startLocation: Location, stopLocation: Location) {
    this(startLocation.getX, startLocation.getY, stopLocation.getX, stopLocation.getY)
  }

  def getStartPoint: Point2D.Double = segment.getP1.asInstanceOf[Point2D.Double]
  def getStopPoint: Point2D.Double = segment.getP2.asInstanceOf[Point2D.Double]
  def getThickness: Float = thickness
  def intersects(rect: Rectangle2D.Double): Boolean = segment.intersects(rect)

  /** returns true if the point lies on the wall */
  def intersects(i: Double, j: Double, eps: Double): Boolean = segment.intersects(i, j, eps, eps)
  def isVertical: Boolean = segment.getX1 == segment.getX2
  def isHorizontal: Boolean = segment.getY1 == segment.getY2
}
