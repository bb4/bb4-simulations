// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import java.awt.Point
import java.awt.Polygon


/**
  * This class contains the three points that define a triangle.
  * @author Barry Becker
  */
class Triangle(var A: Point, var B: Point, var C: Point) {

  /** @return a triangular polygon*/
  def getPoly: Polygon = {
    val triangle = new Polygon
    triangle.addPoint(A.x, A.y)
    triangle.addPoint(B.x, B.y)
    triangle.addPoint(C.x, C.y)
    triangle
  }
}