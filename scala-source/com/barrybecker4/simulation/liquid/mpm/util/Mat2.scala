// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm.util

object Mat2 {
  // Matrix is represented as [a, b, c, d] for:
  // | a b |
  // | c d |
  type Mat2 = (Double, Double, Double, Double)

  def add(a: Mat2, b: Mat2): Mat2 = (a._1 + b._1, a._2 + b._2, a._3 + b._3, a._4 + b._4)
  def sub(a: Mat2, b: Mat2): Mat2 = (a._1 - b._1, a._2 - b._2, a._3 - b._3, a._4 - b._4)
  def mul(a: Mat2, b: Mat2): Mat2 = (
    a._1 * b._1 + a._2 * b._3,
    a._1 * b._2 + a._2 * b._4,
    a._3 * b._1 + a._4 * b._3,
    a._3 * b._2 + a._4 * b._4
  )

  def mulVec(m: Mat2, v: Vec2.Vec2): Vec2.Vec2 = (
    m._1 * v._1 + m._2 * v._2,
    m._3 * v._1 + m._4 * v._2
  )

  def determinant(m: Mat2): Double = m._1 * m._4 - m._2 * m._3

  def transpose(m: Mat2): Mat2 = (m._1, m._3, m._2, m._4)

  def outer(v1: Vec2.Vec2, v2: Vec2.Vec2): Mat2 = (
    v1._1 * v2._1, v1._1 * v2._2,
    v1._2 * v2._1, v1._2 * v2._2
  )

  def map(m: Mat2, f: Double => Double): Mat2 = (f(m._1), f(m._2), f(m._3), f(m._4))
}