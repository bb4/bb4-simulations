// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

/**
  * Pure helpers for mouse-driven terrain/water edits (testable without Swing).
  */
private[water] object InteractionMath {

  val X_SCALE: Int = 10

  /** Inclusive column index range affected by a vertical drag, centered on xpos. */
  def horizontalRange(xpos: Int, absYDiff: Int, width: Int): (Int, Int) = {
    var s1 = xpos - X_SCALE * absYDiff
    if (s1 < 0) s1 = 0
    var s2 = xpos + X_SCALE * absYDiff
    if (s2 >= width) s2 = width - 1
    (s1, s2)
  }

  /**
    * @param gaussAt argument to the error-function kernel (same contract as ErrorFunction.getValue)
    */
  def heightDeltaGaussian(absYDiff: Double, xdiff: Double, gaussAt: Double => Double): Double = {
    0.3 * absYDiff * (1.0 - gaussAt(0.5 * xdiff / absYDiff))
  }
}
