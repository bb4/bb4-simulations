// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.VoronoiEdge
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor


object BreakPoint {
  private def sq(d: Double) = d * d
}

case class BreakPoint(s1: Point, s2: Point, e: VoronoiEdge, isEdgeLeft: Boolean, v: VoronoiProcessor) {
  val edgeBegin: Point = this.getPoint
  private var cacheSweepLoc: Double = .0
  private var cachePoint: Point = _

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
    val seepLocation = v.getSweepLoc
    if (seepLocation == cacheSweepLoc) return cachePoint
    cacheSweepLoc = seepLocation
    var x: Double = .0
    var y: Double = .0
    // Handle the vertical line case
    if (s1.y == s2.y) {
      x = (s1.x + s2.x) / 2.0 // x coordinate is between the two sites

      // comes from parabola focus-directrix definition:
      y = (BreakPoint.sq(x - s1.x) + BreakPoint.sq(s1.y) - BreakPoint.sq(seepLocation)) / (2.0 * (s1.y - seepLocation))
    }
    else { // This method works by intersecting the line of the edge with the parabola of the higher point
      // I'm not sure why I chose the higher point, either should work
      val px = if (s1.y > s2.y) s1.x
      else s2.x
      val py = Math.max(s1.y, s2.y)
      val m = e.m
      val b = e.b
      val d = 2 * (py - seepLocation)
      // Straight up quadratic formula
      val A = 1
      val B = -2 * px - d * m
      val C = BreakPoint.sq(px) + BreakPoint.sq(py) - BreakPoint.sq(seepLocation) - d * b
      val sign = if (s1.y > s2.y) -1
      else 1
      val det = BreakPoint.sq(B) - 4 * A * C
      // When rounding leads to a very small negative determinant, fix it
      if (det <= 0) x = -B / (2 * A)
      else x = (-B + sign * Math.sqrt(det)) / (2 * A)
      y = m * x + b
    }
    cachePoint = new Point(x, y)
    cachePoint
  }

  override def toString: String = String.format("%s \ts1: %s\ts2: %s", this.getPoint, this.s1, this.s2)

  def getEdge: VoronoiEdge = this.e
}
