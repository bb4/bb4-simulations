/* Copyright by Barry G. Becker, 2020. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.ComplexNumber
import GammaFunction._

object GammaFunction {
  private val G = 7
  private val P = Array(
    0.99999999999980993, 676.5203681218851, -1259.1392167224028,
    771.32342877765313, -176.61502916214059, 12.507343278686905,
    -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
  ).map(ComplexNumber(_, 0))
}

/**
  * See https://en.wikipedia.org/wiki/Lanczos_approximation of gamma function
  * Implementation derived from https://rosettacode.org/wiki/Gamma_function
  */
case class GammaFunction() extends ComplexFunction {

  /** n is ignored for gamma function  */
  override def compute(s: ComplexNumber, n: Int): ComplexNumber = {
    if (s.real < 0.5) {
      val denom =
        ComplexNumber.sin(s.multiply(ComplexNumber(Math.PI)))
          .multiply(this.compute(ComplexNumber(1.0).subtract(s), n))
      assert(denom != 0)
      ComplexNumber(Math.PI).divide(denom)
    }
    else {
      val z = s.subtract(1.0)
      var x = P(0)
      for (i <- 1 until G + 2)
        x = x.add(P(i).divide(z.add(i)))
      val t = z.add(G + 0.5)

      val term1 = ComplexNumber.pow(2.0 * Math.PI, ComplexNumber(0.5))
      val term2 = ComplexNumber.pow(t, z.add(0.5))
      val term3 = ComplexNumber.pow(Math.E, t.negate)
      term1.multiply(term2).multiply(term3).multiply(x)
    }
  }
}
