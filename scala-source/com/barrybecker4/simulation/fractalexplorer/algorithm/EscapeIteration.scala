/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumber

/**
  * Shared Mandelbrot/Julia escape-time normalization (iterations / maxIterations).
  */
private[algorithm] object EscapeIteration {

  def normalizedEscapeTime(z0: ComplexNumber, c: ComplexNumber, maxIterations: Int): Double = {
    var z = z0
    var n = 0
    while (z.getMagnitude < 2.0 && n < maxIterations) {
      z = z.power(2).add(c)
      n += 1
    }
    n.toDouble / maxIterations
  }
}
