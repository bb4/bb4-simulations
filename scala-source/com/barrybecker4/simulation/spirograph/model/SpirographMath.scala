/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph.model

import com.barrybecker4.math.MathUtil

/**
  * Pure kinematics for the spirograph curve (period, stepping, pen position).
  * Used by the model/renderer and unit tests; display strings stay in [[ParametricEquations]].
  */
object SpirographMath {

  def sign(r2: Float): Int = if (r2 < 0) -1 else 1

  /** Number of complete revolutions until the curve overwrites itself (integer radii). */
  def numRevolutions(r1: Float, r2: Float): Int = {
    val s = sign(r2)
    val gcd = MathUtil.gcd(r1.toLong, (s * r2).toLong)
    ((s * r2) / gcd).toInt
  }

  /** Segments used for one revolution pass: `1 + numSegmentsPerRev * |pos / r2|`. Caller must ensure `r2 != 0`. */
  def segmentsPerPass(numSegmentsPerRev: Int, pos: Float, r2: Float): Float =
    1.0f + numSegmentsPerRev * Math.abs(pos / r2)

  def segmentLimit(n: Float, revs: Int): Int =
    (n * revs + 0.5).toInt

  def theta(count: Int, n: Float): Float =
    (2.0f * Math.PI * count / n).toFloat

  /** Roll angle on the secondary circle. Caller must ensure `r2 != 0`. */
  def phi(theta: Float, r1: Float, r2: Float): Float =
    theta * (1.0f + r1 / r2)

  def center(width: Int, height: Int, r1: Float, r2: Float, theta: Float): (Double, Double) =
    ((width >> 1) + (r1 + r2) * Math.cos(theta),
      (height >> 1) - (r1 + r2) * Math.sin(theta))

  def penPoint(cx: Double, cy: Double, pos: Float, phi: Float): (Float, Float) =
    ((cx + pos * Math.cos(phi)).toFloat, (cy - pos * Math.sin(phi)).toFloat)
}
