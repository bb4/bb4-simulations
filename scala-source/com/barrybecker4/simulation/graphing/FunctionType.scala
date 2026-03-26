// Copyright by Barry G. Becker, 2016-2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.{ArrayFunction, ErrorFunction, Function}

/**
  * Different types of functions to test.
  * @author Barry Becker
  */
enum FunctionType(val name: String, val function: Function):

  case DIAGONAL extends FunctionType("Two point diagonal", FunctionType.sampled(0.0, 1.0))
  case HORZ_LINE extends FunctionType("Horizontal Line", FunctionType.sampled(1.0, 1.0))
  case VERT_LINE
      extends FunctionType(
        "Vertical Line",
        FunctionType.sampled(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
      )
  case SQUARE
      extends FunctionType(
        "Square",
        FunctionType.sampled(0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0)
      )
  case TEETH
      extends FunctionType(
        "Teeth",
        FunctionType.sampled(0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0)
      )
  case JAGGED extends FunctionType("Jagged", FunctionType.sampled(0.0, .5, 0.1, 0.8, 0.6, 1.0))
  case ERROR extends FunctionType("Error Function", new ErrorFunction)
  case SMOOTH
      extends FunctionType(
        "Smooth Function",
        FunctionType.sampled(0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0)
      )
  case TYPICAL_SMOOTH
      extends FunctionType(
        "Typical Smooth",
        FunctionType.sampled(0.0, .1, 0.3, 0.6, 0.7, 0.75, 0.7, 0.5, 0.4, 0.36, 0.39, 0.45, 0.56, 0.7, 1.0)
      )
  case V extends FunctionType("V Function", FunctionType.sampled(1.0, 0.0, 1.0))

end FunctionType

object FunctionType {

  /** Placeholder inverse map; forward interpolation uses [[functionMap]] only. */
  private val NoInverseMap: Array[Double] = null

  private[graphing] def sampled(values: Double*): ArrayFunction =
    new ArrayFunction(values.toArray, NoInverseMap)
}
