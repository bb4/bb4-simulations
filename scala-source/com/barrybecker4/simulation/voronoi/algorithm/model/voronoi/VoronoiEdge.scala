// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import scala.compiletime.uninitialized


class VoronoiEdge(val site1: Point, val site2: Point) {
  require(
    site1.x != site2.x || site1.y != site2.y,
    "VoronoiEdge sites must be distinct"
  )

  /** True when sites share y — the perpendicular bisector is a vertical line. */
  private val isVerticalBisector = site1.y == site2.y
  /** True when sites share x — the perpendicular bisector is a horizontal line. */
  private val isHorizontalBisector = site1.x == site2.x

  var p1: Point = uninitialized
  var p2: Point = uninitialized
  val midpoint: Point = Point.midpoint(site1, site2)

  // parameters for line that the edge lies on
  var m = .0
  var b = .0

  if (isVerticalBisector) {
    // Vertical line at midpoint x; slope unused in intersection (handled via flag)
    m = 0
    b = 0
  } else if (isHorizontalBisector) {
    m = 0
    b = midpoint.y
  } else {
    m = -1.0 / ((site1.y - site2.y) / (site1.x - site2.x))
    b = midpoint.y - m * midpoint.x
  }

  def getMidpoint: Point = midpoint

  def intersection(that: VoronoiEdge): Point = {
    // Parallel vertical bisectors have no unique intersection
    if (this.isVerticalBisector && that.isVerticalBisector) return null
    // Parallel non-coincident lines
    if (this.m == that.m && this.b != that.b &&
      this.isVerticalBisector == that.isVerticalBisector) return null

    var x = .0
    var y = .0
    if (this.isVerticalBisector) {
      x = (this.site1.x + this.site2.x) / 2
      y = that.m * x + that.b
    }
    else if (that.isVerticalBisector) {
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
