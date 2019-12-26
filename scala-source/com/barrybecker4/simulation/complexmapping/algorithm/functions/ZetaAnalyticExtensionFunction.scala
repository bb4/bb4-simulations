/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.common.math.ComplexNumber


/**
  * This function extends the zeta function so its domain is the whole complex plane.
  * See 35 minutes into https://www.youtube.com/watch?v=YuIIjLr6vUA&feature=emb_rel_pause
  * zeta(z) = eta(z) / (1 - 2/2&caret;z)
  * The only place where it is not defined is for z = 1.
  *
  * For example,
  * zeta(0) = 1/2 / (-1) = -1/2
  * zeta(-1) = -1/12
  * zeta(-2) = 0
  * zeta(-3) = 1/120
  * zeta(-4) = 0
  * The trivial zeros are for z = 0, -2, -4, -6, ...
  * All the non-trivial zeroes are hypothesized (by Rieman) to lie on z = 1/2
  *
  */
case class ZetaAnalyticExtensionFunction() extends ComplexFunction {

  val etaFun = DirichletEtaFunction()

  /** @param s complex number
    * @param n the number of iterations. The larger this is, the more accurate the result.
    * @return the result of applying the function
    */
  override def compute(s: ComplexNumber, n: Int): ComplexNumber = {
    val num = etaFun.compute(s, n)
    val denominator = ComplexNumber(1.0, 0).subtract(ComplexNumber(2.0, 0).divide(ComplexNumber.pow(2.0, s)))
    num.divide(denominator)
  }
}
