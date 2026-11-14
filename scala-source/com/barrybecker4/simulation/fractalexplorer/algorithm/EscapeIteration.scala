/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumber

/**
  * Shared Mandelbrot/Julia escape-time normalization (iterations / maxIterations).
  * Hot path uses local doubles and squared-magnitude checks to avoid Complex allocations.
  */
private[algorithm] object EscapeIteration {

  /** Escape radius squared (|z| < 2 ⇔ |z|² < 4). */
  private val EscapeRadiusSq = 4.0

  def normalizedEscapeTime(z0: ComplexNumber, c: ComplexNumber, maxIterations: Int): Double = {
    var zr = z0.real
    var zi = z0.imaginary
    val cr = c.real
    val ci = c.imaginary
    var n = 0
    var magSq = zr * zr + zi * zi
    while (magSq < EscapeRadiusSq && n < maxIterations) {
      // z = z² + c
      val newZr = zr * zr - zi * zi + cr
      zi = 2.0 * zr * zi + ci
      zr = newZr
      magSq = zr * zr + zi * zi
      n += 1
    }
    n.toDouble / maxIterations
  }
}
