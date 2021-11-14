// Copyright by Barry G. Becker, 2016-2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.math.function.ArrayFunction
import com.barrybecker4.math.function.ErrorFunction
import com.barrybecker4.math.function.Function
import com.barrybecker4.simulation.graphing.FuncConsts.NULL_FUNC


/**
  * Different types of functions to test.
  * @author Barry Becker
  */
enum FunctionType(val name: String, val function: Function):

  case DIAGONAL extends FunctionType("Two point diagonal", new ArrayFunction(Array(0.0, 1.0),
    NULL_FUNC))
  case HORZ_LINE extends FunctionType("Horizontal Line", new ArrayFunction(Array(1.0, 1.0),
    NULL_FUNC))
  case VERT_LINE extends FunctionType("Vertical Line", new ArrayFunction(Array(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
    NULL_FUNC))
  case SQUARE extends FunctionType("Square", new ArrayFunction(Array(0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0),
    NULL_FUNC))
  case TEETH extends FunctionType("Teeth", new ArrayFunction(Array(0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0),
    NULL_FUNC))
  case JAGGED extends FunctionType("Jagged", new ArrayFunction(Array(0.0, .5, 0.1, 0.8, 0.6, 1.0),
    NULL_FUNC))
  case ERROR extends FunctionType("Error Function", new ErrorFunction)
  case SMOOTH extends FunctionType("Smooth Function",
    new ArrayFunction(Array(0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0),
    NULL_FUNC))
  case TYPICAL_SMOOTH extends FunctionType("Typical Smooth",
    new ArrayFunction(Array(0.0, .1, 0.3, 0.6, 0.7, 0.75, 0.7, 0.5, 0.4, 0.36, 0.39, 0.45, 0.56, 0.7, 1.0),
    NULL_FUNC))
  case V extends FunctionType("V Function", new ArrayFunction(Array(1.0, 0.0, 1.0),
    NULL_FUNC))
  
end FunctionType

