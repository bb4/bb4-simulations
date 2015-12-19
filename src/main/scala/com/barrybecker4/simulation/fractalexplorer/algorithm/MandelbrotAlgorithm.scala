/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.common.math.ComplexNumber
import com.barrybecker4.common.math.ComplexNumberRange

/**
  * Populates the FractalModel using the iterative Mandelbrot algorithm..
  * @author Barry Becker
  */
object MandelbrotAlgorithm {
  private val INITIAL_RANGE: ComplexNumberRange =
    new ComplexNumberRange(new ComplexNumber(-1.1601118453945314, 0.26972512394950576), new ComplexNumber(-1.1601108213311082, 0.2697274120216164))
    //new ComplexNumberRange(new ComplexNumber(-2.1, -1.5), new ComplexNumber(1.1, 1.5))
}

class MandelbrotAlgorithm(model: FractalModel) extends FractalAlgorithm(model, MandelbrotAlgorithm.INITIAL_RANGE) {

  model.setCurrentRow(0)

  def getFractalValue(initialValue: ComplexNumber): Double = {
    var z: ComplexNumber = initialValue
    var numIterations: Int = 0
    while (z.getMagnitude < 2.0 && numIterations < getMaxIterations) {
      z = z.power(2).add(initialValue)
      numIterations += 1
    }
    numIterations.toDouble / getMaxIterations
  }
}
