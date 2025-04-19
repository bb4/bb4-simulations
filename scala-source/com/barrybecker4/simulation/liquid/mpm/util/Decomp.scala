// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm.util

object Decomp {
  import Mat2._
  import Vec2._

  // Simplified polar decomposition
  def polar(F: Mat2): (Mat2, Mat2) = {
    // SVD decomposition simplified
    val C = mul(transpose(F), F)
    val trace = C._1 + C._4
    val det = determinant(C)

    val lambda1 = trace/2 + Math.sqrt((trace*trace)/4 - det)
    val lambda2 = trace/2 - Math.sqrt((trace*trace)/4 - det)

    // Approximate rotation matrix
    val h = Math.sqrt(F._1 * F._1 + F._3 * F._3)
    if (h < 1e-10) {
      // Identity rotation
      ((1.0, 0.0, 0.0, 1.0), F)
    } else {
      val R = (F._1 / h, F._2 / h, F._3 / h, F._4 / h)
      val S = mul(transpose(R), F)
      (R, S)
    }
  }
}