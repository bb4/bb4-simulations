/*
 * Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.common.math.ComplexNumber


/**
  * See 35 minutes into https://www.youtube.com/watch?v=YuIIjLr6vUA&feature=emb_rel_pause
  * zeta(z) = eta(z) / (1 - 2/2&caret;z)
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
    println("eta num = " + num)
    num.divide(denominator)
  }
}
