// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.{ArrayFunction, ErrorFunction, Function}

/**
  * Different types of functions to plot in the graphing demo.
  */
enum GraphFunctionType(val name: String, val function: Function):

  case DIAGONAL extends GraphFunctionType("Two point diagonal", GraphFunctionType.sampled(0.0, 1.0))
  case HORZ_LINE extends GraphFunctionType("Horizontal Line", GraphFunctionType.sampled(1.0, 1.0))
  case STEP_UP
      extends GraphFunctionType(
        "Step Up",
        GraphFunctionType.sampled(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
      )
  case SQUARE
      extends GraphFunctionType(
        "Square",
        GraphFunctionType.sampled(0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0)
      )
  case TEETH
      extends GraphFunctionType(
        "Teeth",
        GraphFunctionType.sampled(0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0)
      )
  case JAGGED extends GraphFunctionType("Jagged", GraphFunctionType.sampled(0.0, .5, 0.1, 0.8, 0.6, 1.0))
  case ERROR extends GraphFunctionType("Error Function", new ErrorFunction)
  case SMOOTH
      extends GraphFunctionType(
        "Smooth Function",
        GraphFunctionType.sampled(0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0)
      )
  case TYPICAL_SMOOTH
      extends GraphFunctionType(
        "Typical Smooth",
        GraphFunctionType.sampled(0.0, .1, 0.3, 0.6, 0.7, 0.75, 0.7, 0.5, 0.4, 0.36, 0.39, 0.45, 0.56, 0.7, 1.0)
      )
  case V extends GraphFunctionType("V Function", GraphFunctionType.sampled(1.0, 0.0, 1.0))

end GraphFunctionType

object GraphFunctionType {

  private[graphing] def sampled(values: Double*): ArrayFunction =
    ArrayFunction.fromSamples(values.toArray)
}
