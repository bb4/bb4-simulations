// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor


object BreakPoint {
  private def sq(d: Double) = d * d

  /** Breakpoint when the two sites share the same y (beach line meets vertical bisector region). */
  def pointWhenSitesHorizontal(s1: Point, s2: Point, sweepLoc: Double): Point = {
    val x = (s1.x + s2.x) / 2.0
    val y = (sq(x - s1.x) + sq(s1.y) - sq(sweepLoc)) / (2.0 * (s1.y - sweepLoc))
    new Point(x, y)
  }

  /** Intersect the edge line with the parabola of the higher site for general position. */
  def pointWhenSitesGeneral(s1: Point, s2: Point, e: VoronoiEdge, sweepLoc: Double): Point = {
    val px = if (s1.y > s2.y) s1.x else s2.x
    val py = math.max(s1.y, s2.y)
    val m = e.m
    val b = e.b
    val d = 2.0 * (py - sweepLoc)
    val A = 1.0
    val B = -2 * px - d * m
    val C = sq(px) + sq(py) - sq(sweepLoc) - d * b
    val sign = if (s1.y > s2.y) -1 else 1
    val det = sq(B) - 4 * A * C
    val x =
      if (det <= 0) -B / (2 * A)
      else (-B + sign * math.sqrt(det)) / (2 * A)
    val y = m * x + b
    new Point(x, y)
  }
}

case class BreakPoint(s1: Point, s2: Point, e: VoronoiEdge, isEdgeLeft: Boolean, v: VoronoiProcessor) {
  val edgeBegin: Point = this.getPoint
  private var cacheSweepLoc: Double = .0
  private var cachePoint: Point = null

  def finish(vert: Point): Unit = {
    if (isEdgeLeft) this.e.p1 = vert
    else this.e.p2 = vert
  }

  def finish(): Unit = {
    val p = this.getPoint
    if (isEdgeLeft) this.e.p1 = p
    else this.e.p2 = p
  }

  def getPoint: Point = {
    val sweepLoc = v.getSweepLoc
    if (sweepLoc == cacheSweepLoc && cachePoint != null) return cachePoint
    cacheSweepLoc = sweepLoc
    cachePoint =
      if (s1.y == s2.y) BreakPoint.pointWhenSitesHorizontal(s1, s2, sweepLoc)
      else BreakPoint.pointWhenSitesGeneral(s1, s2, e, sweepLoc)
    cachePoint
  }

  override def toString: String = String.format("%s \ts1: %s\ts2: %s", this.getPoint, this.s1, this.s2)

  def getEdge: VoronoiEdge = this.e
}
