/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.common.math.ComplexNumber

/**
  * Calculate fractal values for a row.
  * Use run magnitude optimization if specified.
  * @author Barry Becker
  */
object RowCalculator {
  /** max run magnitude before we start skipping values. */
  private val M: Int = 4
  /** Amount to skip until we find a mismatch, then back up this amount. */
  private val N: Int = 7
}

class RowCalculator(algorithmToUse: FractalAlgorithm) {
  private val algorithm: FractalAlgorithm = algorithmToUse
  private val model: FractalModel = algorithm.getModel
  private var useRunLengthOpt =  false

  def getUseRunLengthOptimization: Boolean = useRunLengthOpt

  def setUseRunLengthOptimization(value: Boolean) {
    if (useRunLengthOpt != value) {
      useRunLengthOpt = value
      model.setCurrentRow(0)
    }
  }

  /** Computes values for a row. */
  def calculateRow(width: Int, y: Int) {
    if (useRunLengthOpt) calculateRowOptimized(width, y)
    else calculateRowSimple(width, y)
  }

  /** Computes values for a row. */
  private def calculateRowSimple(width: Int, y: Int) {
    var x: Int = 0
    while (x < width) {
       computeFractalValueForPosition(x, y)
       x += 1
    }
  }

  /**
    * Computes values for a row.
    * Take advantage of run lengths if turned on.
    * If using the run magnitude optimization, the results may not be absolutely perfect,
    * but anomalies should be very rare.
    * This can give 2x or more speedup when the MAX ITERATIONS is reasonably high.
    *
    * The run magnitude algorithm works like this.
    * March along until we find a run of M identical values. From that point evaluate only every Nth pixel
    * until we find a different value.
    * When that happens, back up N  pixels and evaluate each one again.
    * Need something to avoid skipping the tips of long thin triangular sections.
    */
  private def calculateRowOptimized(width: Int, y: Int) {
    var runLength: Int = 0
    var increment: Int = 1
    var lastValue: Double = 0
    var x: Int = 0
    while (x < width) {
      var currentValue: Double = computeFractalValueForPosition(x, y)
      if (lastValue == currentValue) runLength += increment
      else {
        lastValue = currentValue
        if (runLength > RowCalculator.M) {
          x -= (RowCalculator.N - 1)
          currentValue = computeFractalValueForPosition(x, y)
          increment = 1
        }
        runLength = 0
      }
      if (runLength > RowCalculator.M) {
        increment = RowCalculator.N
        var xx: Int = x
        while (xx < x + RowCalculator.N) {
          model.setValue(xx, y, currentValue)
          xx += 1; xx - 1
        }
      }
      model.setValue(x, y, currentValue)
      x += increment
    }
  }

  /**
    * @return the fractal value that was set into the model.
    */
  private def computeFractalValueForPosition(x: Int, y: Int): Double = {
    val z: ComplexNumber = algorithm.getComplexPosition(x, y)
    val value: Double = algorithm.getFractalValue(z)
    model.setValue(x, y, value)
    value
  }
}
