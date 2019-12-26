/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.common.math.ComplexNumber


/**
  * See 35 minutes into https://www.youtube.com/watch?v=YuIIjLr6vUA&feature=emb_rel_pause
  * zeta(z) = eta(z) / (1 - 2/2&caret;z)
  */
case class DirichletEtaFunction() extends ComplexFunction {

  /** @param s complex number
    * @param n the number of iterations. The larger this is, the more accurate the result.
    * @return the result of applying the function
    */
  override def compute(s: ComplexNumber, n: Int): ComplexNumber = {
    var sum: ComplexNumber = ComplexNumber(0, 0)
    for (i <- 1 to n) {
      val i2 = (2 * i).toDouble
      val term1 = ComplexNumber.pow(i2 - 1, s).reciprocal
      val term2 = ComplexNumber.pow(i2, s).reciprocal
      // println("adding " + term1 + " - " + term2 + " to sum = " + sum)
      sum = sum.add(term1).subtract(term2)
    }

    // instead of summing directly, we need to do the supersum calculation described in
    // https://www.youtube.com/watch?v=YuIIjLr6vUA&feature=emb_rel_pause about 19 minutes in.
    sum
  }
}
