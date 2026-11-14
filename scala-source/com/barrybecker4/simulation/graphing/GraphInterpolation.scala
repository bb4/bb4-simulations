// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.{ArrayFunction, ErrorFunction, Function}
import com.barrybecker4.math.interpolation.InterpolationMethod

/**
  * Applies the selected interpolation method to a [[Function]] for graphing.
  * Pure logic shared by [[GraphOptionsDialog]] and tests.
  */
object GraphInterpolation {

  def apply(base: Function, method: InterpolationMethod): Function =
    base match {
      case af: ArrayFunction => af.withInterpolationMethod(method)
      case _: ErrorFunction => base
      case other =>
        throw new IllegalArgumentException("Unexpected function type: " + other.getClass.getName)
    }
}
