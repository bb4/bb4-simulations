// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model.util

object Vec2 {
  type Vec2 = (Double, Double)

  def add(a: Vec2, b: Vec2): Vec2 = (a._1 + b._1, a._2 + b._2)
  def sub(a: Vec2, b: Vec2): Vec2 = (a._1 - b._1, a._2 - b._2)
  def scale(a: Vec2, s: Double): Vec2 = (a._1 * s, a._2 * s)
  def distance(a: Vec2, b: Vec2): Double = {
    val dx = a._1 - b._1
    val dy = a._2 - b._2
    Math.sqrt(dx * dx + dy * dy)
  }
  def length(a: Vec2): Double = Math.sqrt(a._1 * a._1 + a._2 * a._2)
}