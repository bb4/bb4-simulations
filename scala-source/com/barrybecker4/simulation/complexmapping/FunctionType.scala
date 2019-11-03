/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping

import com.barrybecker4.common.math.function.{ArrayFunction, ErrorFunction, Function}
import com.barrybecker4.simulation.complexmapping.functions.{ComplexFunction, RiemannZetaFunction, SquaredFunction}


/**
  * Some functions that we can use to map complex numbers to new locations.
  * @author Barry Becker
  */
object FunctionType extends Enumeration {

  case class Val(name: String, function: ComplexFunction) extends super.Val
  implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]


  val SQUARED = Val("s ^ 2", SquaredFunction())
  val RIEMANN = Val("Riemann Zeta", RiemannZetaFunction(10))

  val VALUES: Array[Val] = Array(
    SQUARED, RIEMANN
  )
}
