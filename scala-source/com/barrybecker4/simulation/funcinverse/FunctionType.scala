// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.funcinverse

import scala.collection.immutable.Range
import Math._

/**
  * Different types of functions to find the inverses of
  * @author Barry Becker
  */
object FunctionType extends Enumeration {

  case class Val(name: String, func: Array[Double]) extends super.Val

  //implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]
  val LINEAR = Val("Linear", Array(0, 0.5, 1.0))
  val ALMOST_LINEAR = Val("AlmostLinear", Array(0, 0.45, 0.55, 1.0))
  val QUADRATIC = Val("Quadratic", Array(0, 1.0/16.0, 1/4.0, 9.0/16.0, 1.0))
  val EXPONENTIAL = Val("Exponential", Array(0, 1/16.0, 2/16.0, 1/4.0, 1/2.0, 1.0))
  val SINE = Val("Sine", Range.BigDecimal(0, PI/2 + PI/200, PI/200).map(x => sin(x.toDouble)).toArray)

  val VALUES: Array[Val] = Array(ALMOST_LINEAR, LINEAR, QUADRATIC, EXPONENTIAL, SINE)
}
