/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.math.complex.ComplexNumberRange

/**
  * Populates the FractalModel using the iterative Mandelbrot algorithm.
  * @author Barry Becker
  */
object MandelbrotAlgorithm {
  private val INITIAL_RANGE: ComplexNumberRange =
   ComplexNumberRange(new ComplexNumber(-2.1, -1.5), new ComplexNumber(1.1, 1.5))
}

class MandelbrotAlgorithm extends FractalAlgorithm(MandelbrotAlgorithm.INITIAL_RANGE) {

  def getFractalValue(initialValue: ComplexNumber): Double =
    EscapeIteration.normalizedEscapeTime(initialValue, initialValue, getMaxIterations)
}
