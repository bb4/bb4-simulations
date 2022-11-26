// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point


class VoronoiEdge(val site1: Point, val site2: Point) {
  private val isVertical = site1.y == site2.y
  var p1: Point = _
  var p2: Point = _

  // parameters for line that the edge lies on
  var m = .0
  var b = .0

  if (isVertical) {
    m = 0
    b = 0
  }
  else {
    m = -1.0 / ((site1.y - site2.y) / (site1.x - site2.x))
    val midpoint = Point.midpoint(site1, site2)
    b = midpoint.y - m * midpoint.x
  }
  

  def intersection(that: VoronoiEdge): Point = {
    if (this.m == that.m && this.b != that.b && this.isVertical == that.isVertical) return null // no intersection
    var x = .0
    var y = .0
    if (this.isVertical) {
      x = (this.site1.x + this.site2.x) / 2
      y = that.m * x + that.b
    }
    else if (that.isVertical) {
      x = (that.site1.x + that.site2.x) / 2
      y = this.m * x + this.b
    }
    else {
      x = (that.b - this.b) / (this.m - that.m)
      y = m * x + b
    }
    new Point(x, y)
  }
}
