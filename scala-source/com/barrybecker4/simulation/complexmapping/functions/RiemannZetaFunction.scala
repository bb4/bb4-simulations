/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.functions

import com.barrybecker4.common.math.ComplexNumber

/**
  * Compute https://en.wikipedia.org/wiki/Riemann_zeta_function for a specified n
  * @param n the number of iterations. The larger this is, the more accurate the result.
  */
case class RiemannZetaFunction(n: Int) extends ComplexFunction {

  override def compute(s: ComplexNumber): ComplexNumber = {
    var sum: ComplexNumber = ComplexNumber(0, 0)
    for (i <- 1 to n)
      sum = sum.add(ComplexNumber.pow(n, s))
    sum
  }
}
