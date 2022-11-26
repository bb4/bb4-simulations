// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor


class Arc(var left: BreakPoint, var right: BreakPoint, val site: Point, val v: VoronoiProcessor) extends ArcKey {

  def this(left: BreakPoint, right: BreakPoint, v: VoronoiProcessor) = {
    this(left, right, if (left != null) left.s2 else right.s1, v)
  }

  // Only for creating the first Arc
  def this(site: Point, v: VoronoiProcessor) = {
    this(null, null, site, v)
  }

  override def getRight: Point = {
    if (right != null) return right.getPoint
    new Point(Double.PositiveInfinity, Double.PositiveInfinity)
  }

  override def getLeft: Point = {
    if (left != null) return left.getPoint
    new Point(Double.NegativeInfinity, Double.PositiveInfinity)
  }

  def getSweepLoc: Double = v.getSweepLoc

  override def toString: String = {
    val l = getLeft
    val r = getRight
    String.format("{%.4f, %.4f}", l.x, r.x)
  }

  def checkCircle: Point = {
    if ((this.left == null) || (this.right == null)) return null
    if (Point.ccw(this.left.s1, this.site, this.right.s2) != -1) return null
    this.left.getEdge.intersection(this.right.getEdge)
  }
}