// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

/**
  * Different types of functions to find the inverses of
  * @author Barry Becker
  */
object FunctionType extends Enumeration {

  case class Val(name: String, func: Array[Double]) extends super.Val

  //implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]
  val LINEAR = Val("Linear", Array(0, 1.0))
  val QUADRATIC = Val("Quadratic", Array(0, 1.0, 4.0, 9.0, 16.0))
  val EXPONENTIAL = Val("Exponential", Array(0, 1.0, 2.0, 4.0, 8.0, 16.0))

  val VALUES: Array[Val] = Array(LINEAR, QUADRATIC, EXPONENTIAL)
}
