/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import javax.vecmath.Point2d


/**
  * Marching-squares style zero-contour segments for a scalar field on a rectilinear grid.
  */
object DomainContour {

  /** Line segment in domain coordinates. */
  case class Segment(a: Point2d, b: Point2d)

  /**
    * Build contour segments where the scalar field crosses zero.
    * @param values row-major samples: values(ix)(iy); x increases with ix, y with iy
    * @param xs x coordinates for each ix
    * @param ys y coordinates for each iy
    */
  def zeroContours(values: Array[Array[Double]], xs: Array[Double], ys: Array[Double]): Seq[Segment] = {
    if (values.isEmpty || values(0).isEmpty) return Seq.empty
    val nx = values.length
    val ny = values(0).length
    val segs = scala.collection.mutable.ArrayBuffer.empty[Segment]
    for (i <- 0 until nx - 1; j <- 0 until ny - 1) {
      val v00 = values(i)(j)
      val v10 = values(i + 1)(j)
      val v11 = values(i + 1)(j + 1)
      val v01 = values(i)(j + 1)
      if (isFinite(v00) && isFinite(v10) && isFinite(v11) && isFinite(v01)) {
        val corners = Array(
          (xs(i), ys(j), v00),
          (xs(i + 1), ys(j), v10),
          (xs(i + 1), ys(j + 1), v11),
          (xs(i), ys(j + 1), v01)
        )
        val edges = Array((0, 1), (1, 2), (2, 3), (3, 0))
        val crossings = edges.flatMap { case (a, b) =>
          edgeCrossing(corners(a), corners(b))
        }
        if (crossings.length == 2)
          segs += Segment(crossings(0), crossings(1))
        else if (crossings.length == 4) {
          // Ambiguous saddle: connect adjacent pairs
          segs += Segment(crossings(0), crossings(1))
          segs += Segment(crossings(2), crossings(3))
        }
      }
    }
    segs.toSeq
  }

  private def isFinite(v: Double): Boolean = v.isFinite

  /** Linear interpolate zero crossing on an edge if signs differ. */
  private[algorithm] def edgeCrossing(
      a: (Double, Double, Double),
      b: (Double, Double, Double)
  ): Option[Point2d] = {
    val (_, _, va) = a
    val (_, _, vb) = b
    if (va == 0.0) Some(new Point2d(a._1, a._2))
    else if (vb == 0.0) Some(new Point2d(b._1, b._2))
    else if (va * vb < 0) {
      val t = va / (va - vb)
      Some(new Point2d(a._1 + t * (b._1 - a._1), a._2 + t * (b._2 - a._2)))
    } else None
  }
}
