// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import java.awt.Point
import java.awt.Polygon


/**
  * The three vertices of a triangle.
  * @author Barry Becker
  */
case class Triangle(a: Point, b: Point, c: Point):

  /** @return a triangular polygon */
  def getPoly: Polygon =
    val poly = new Polygon
    poly.addPoint(a.x, a.y)
    poly.addPoint(b.x, b.y)
    poly.addPoint(c.x, c.y)
    poly

object Triangle:

  def midpoint(p1: Point, p2: Point): Point =
    new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
