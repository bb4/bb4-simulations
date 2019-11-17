/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.common.math.ComplexNumber


/**
  * Compute https://en.wikipedia.org/wiki/Riemann_zeta_function for a specified n.
  * This function is not defined when R < 1. If those values are passed, have them map to ComplexNumber.NaN.
  * @param n the number of iterations. The larger this is, the more accurate the result.
  */
case class RiemannZetaFunction(n: Int) extends ComplexFunction {

  override def compute(s: ComplexNumber): ComplexNumber = {
    if (s.real < 1) ComplexNumber(Double.NaN, Double.NaN)
    else {
      var sum: ComplexNumber = ComplexNumber(0, 0)
      for (i <- 1 to n)
        sum = sum.add(ComplexNumber.pow(i.toDouble, s).reciprocal)
      // if (Math.random() < 0.1) println("sum=" + sum)
      sum
    }
  }
}
