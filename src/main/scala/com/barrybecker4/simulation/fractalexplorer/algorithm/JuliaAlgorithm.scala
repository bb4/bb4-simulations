/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.common.math.ComplexNumber
import com.barrybecker4.common.math.ComplexNumberRange
//import com.barrybecker4.simulation.fractalexplorer.algorithm.{FractalModel, FractalAlgorithm}

/**
  * Populates the FractalModel using the iterative Julia set algorithm.
  *
  * @author Barry Becker
  */
class JuliaAlgorithm(model: FractalModel) extends FractalAlgorithm(model, JuliaAlgorithm.INITIAL_RANGE) {

  private var seed: ComplexNumber = JuliaAlgorithm.DEFAULT_JULIA_SEED


  model.setCurrentRow(0)
  /*
  def this(model: FractalModel, range:ComplexNumberRange) {
    this()
    //`super`(model, JuliaAlgorithm.INITIAL_RANGE)
    model.setCurrentRow(0)
  }  */

  def setJuliaSeed(seed: ComplexNumber) {
    System.out.println("setting jSeed to " + seed)
    this.seed = seed
  }

  def getFractalValue(initialValue: ComplexNumber): Double = {
    var z: ComplexNumber = initialValue
    var numIterations: Int = 0
    while (z.getMagnitude < 2.0 && numIterations < getMaxIterations) {
      z = z.power(2).add(seed)
      numIterations += 1
    }
    numIterations.toDouble / getMaxIterations
  }
}

// for static properties
object JuliaAlgorithm {
  val DEFAULT_JULIA_SEED: ComplexNumber = new ComplexNumber(0.233, 0.5378)
  private val INITIAL_RANGE: ComplexNumberRange = new ComplexNumberRange(new ComplexNumber(-1.8, -1.7), new ComplexNumber(1.8, 1.7))
}
