/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT*/
package com.barrybecker4.simulation.complexmapping.algorithm

import com.barrybecker4.simulation.complexmapping.algorithm.functions._


/**
  * Some functions that we can use to map complex numbers to new locations.
  * @author Barry Becker
  */
object FunctionType extends Enumeration {

  case class Val(name: String, function: ComplexFunction) extends super.Val
  implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]

  val IDENTITY = Val("Identity", IdentityFunction())
  val SQUARED = Val("s ^ 2", SquaredFunction())
  val RIEMANN = Val("Riemann Zeta", RiemannZetaFunction(10))

  val VALUES: Array[Val] = Array(
    IDENTITY, SQUARED, RIEMANN
  )
}
