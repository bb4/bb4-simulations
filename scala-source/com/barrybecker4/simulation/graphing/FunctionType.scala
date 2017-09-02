// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.graphing

import com.barrybecker4.common.math.function.ArrayFunction
import com.barrybecker4.common.math.function.ErrorFunction
import com.barrybecker4.common.math.function.Function


/**
  * Different types of functions to test.
  * @author Barry Becker
  */
object FunctionType extends Enumeration {

  protected case class Val(name: String, function: Function) extends super.Val

  implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]

  private val NULL_FUNC = null

  val DIAGONAL = Val("Two point diagonal", new ArrayFunction(Array(0.0, 1.0), NULL_FUNC))
  val HORZ_LINE = Val("Horizontal Line", new ArrayFunction(Array(1.0, 1.0), NULL_FUNC))
  val VERT_LINE = Val("Vertical Line", new ArrayFunction(Array(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), NULL_FUNC))
  val SQUARE = Val("Square", new ArrayFunction(Array(0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0), NULL_FUNC))
  val TEETH = Val("Teeth", new ArrayFunction(Array(0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0), NULL_FUNC))
  val JAGGED = Val("Jaggen", new ArrayFunction(Array(0.0, .5, 0.1, 0.8, 0.6, 1.0), NULL_FUNC))
  val ERROR = Val("Error Function", new ErrorFunction)

  val SMOOTH = Val("Smooth Function", new ArrayFunction(Array(0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0), NULL_FUNC))
  val TYPICAL_SMOOTH = Val("Typical Smooth",
    new ArrayFunction(Array(0.0, .1, 0.3, 0.6, 0.7, 0.75, 0.7, 0.5, 0.4, 0.36, 0.39, 0.45, 0.56, 0.7, 1.0), NULL_FUNC))
  val V = Val("V Function", new ArrayFunction(Array(1.0, 0.0, 1.0), NULL_FUNC))
}