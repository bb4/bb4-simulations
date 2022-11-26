// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point


class Parabola(val a: Double, val b: Double, val c: Double) {
  
  def this(focus: Point, c: Double) = {
    this(focus.x, focus.y, c)
  }
}
