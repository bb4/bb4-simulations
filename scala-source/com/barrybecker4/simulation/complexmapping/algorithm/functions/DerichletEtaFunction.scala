/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.common.math.ComplexNumber


/**
  * See
  * @param n the number of iterations. The larger this is, the more accurate the result.
  */
case class DerichletEtaFunction() extends ComplexFunction {

  override def compute(s: ComplexNumber, n: Int): ComplexNumber = {
    var sum: ComplexNumber = ComplexNumber(0, 0)
    for (i <- 1 to n) {
      val i2 = (2 * i).toDouble
      val term1 = ComplexNumber.pow(i2 - 1, s).reciprocal
      val term2 = ComplexNumber.pow(i2, s).reciprocal
      sum = sum.add(term1).subtract(term2)
    }
    //if (Math.random() < 0.1) println("sum=" + sum)
    val denominator = ComplexNumber(1.0, 0).subtract(ComplexNumber(2.0, 0).divide(ComplexNumber.pow(2.0, s)))
    sum.divide(denominator)
  }
}
