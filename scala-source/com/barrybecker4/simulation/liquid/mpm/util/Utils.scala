// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm.util

object Utils {
  // Quadratic B-spline kernel functions
  def createKernel(fx: Vec2.Vec2): Array[Array[Double]] = {
    val w = Array.ofDim[Double](3, 2)

    for (i <- 0 until 3) {
      val d = (i - fx._1).abs
      w(i)(0) = if (d < 0.5) 0.75 - d * d else 0.5 * Math.pow(1.5 - d, 2)
    }

    for (j <- 0 until 3) {
      val d = (j - fx._2).abs
      w(j)(1) = if (d < 0.5) 0.75 - d * d else 0.5 * Math.pow(1.5 - d, 2)
    }

    w
  }
}