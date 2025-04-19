// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm.util

object Vec3 {
  type Vec3 = (Double, Double, Double)

  def add(a: Vec3, b: Vec3): Vec3 = (a._1 + b._1, a._2 + b._2, a._3 + b._3)
  def scale(a: Vec3, s: Double): Vec3 = (a._1 * s, a._2 * s, a._3 * s)
}