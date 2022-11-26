// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi


object Point {
  def minYOrderedCompareTo(p1: Point, p2: Point): Int = {
    if (p1.y < p2.y) return 1
    if (p1.y > p2.y) return -1
    if (p1.x == p2.x) return 0
    if (p1.x < p2.x) -1
    else 1
  }

  def midpoint(p1: Point, p2: Point): Point = {
    val x = (p1.x + p2.x) / 2
    val y = (p1.y + p2.y) / 2
    new Point(x, y)
  }

  /**
    * Is (a -> b -> c) a counterclockwise turn?
    *
    * @param a first point
    * @param b second point
    * @param c third point
    * @return { -1, 0, +1 } if (a->b->c) is a { clockwise, collinear; counterclocwise } turn.
    *
    * Copied from Point2D in Algs4
    */
  def ccw(a: Point, b: Point, c: Point): Int = {
    val area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
    if (area2 < 0) -1
    else if (area2 > 0) +1
    else 0
  }

  private val EPSILON = 0.0000001

  private def equals(a: Double, b: Double): Boolean = {
    if (a == b) return true
    Math.abs(a - b) < EPSILON * Math.max(Math.abs(a), Math.abs(b))
  }
}

case class Point(x: Double, y: Double) extends Comparable[Point] {
  override def compareTo(o: Point): Int = {
    if ((this.x == o.x) || (this.x.isNaN && o.x.isNaN)) {
      if (this.y == o.y) return 0
      return if (this.y < o.y) -1
      else 1
    }
    if (this.x < o.x) -1
    else 1
  }

  override def toString: String = String.format("(%.3f, %.3f)", this.x, this.y)

  def distanceTo(that: Point): Double = {
    val xDiff = this.x - that.x
    val yDiff = this.y - that.y
    Math.sqrt(xDiff * xDiff + yDiff * yDiff)
  }

  override def equals(other: Any): Boolean = other.isInstanceOf[Point] && Point.equals(other.asInstanceOf[Point].x, this.x) && Point.equals(other.asInstanceOf[Point].y, this.y)
}
