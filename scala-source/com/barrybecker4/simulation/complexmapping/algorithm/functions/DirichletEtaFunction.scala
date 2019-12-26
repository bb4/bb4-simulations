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
    if (s.real > 1.0) computeConvergent(s, n)
    else computeWithSuperSum(s, n)
  }

  /** When s.real > 1, we can use regular sums because the series converges
    */
  private def computeConvergent(s: ComplexNumber, n: Int): ComplexNumber = {
    var sum: ComplexNumber = ComplexNumber(0, 0)
    for (i <- 1 to n) {
      val i2 = (2 * i).toDouble
      val term1 = ComplexNumber.pow(i2 - 1, s).reciprocal
      val term2 = ComplexNumber.pow(i2, s).reciprocal
      sum = sum.add(term1).subtract(term2)
    }
    sum
  }

  /**
    * If the sequence of partial sums does not converge, then use trickery to build a second sequence from the first.
    * - The nth term of this new sequence is the average of the first n terms of the original sequence.
    *
    * For example, when s = -1, then
    * eta(-1) = 1 - 2 + 3 - 4 + 5 - 6 - 7 ...
    *
    * The derived series is
    *   -1, -1, -1, -1, -1
    *   The super sum sequence is the same, so super-sum is -1
    *
    * When s = 0, we have
    * eta(0) = 1 - 1 + 1 - 1 + 1 - 1 + ...
    * then the nth sums do not diverge - they are:
    * 1 + 0 + 1 + 0 + 1 + 0 +
    * then the nth term in the derived series is the average of the sum of the first n terms.
    *  equals 1 + 1/2 + 2/3 + 1/2 + 3/5 + 1/2 + 4/7 + 1/2 + 5/9 ...
    *
    * In some cases even this derived series may not converge. In those cases, we have to generate another one
    * (or maybe more) until it converges (if it ever does).
    */
  private def computeWithSuperSum(s: ComplexNumber, n: Int): ComplexNumber = {
    var sum: ComplexNumber = ComplexNumber(0, 0)
    for (i <- 1 to n) {
      val i2 = (2 * i).toDouble
      val term1 = ComplexNumber.pow(i2 - 1, s).reciprocal
      val term2 = ComplexNumber.pow(i2, s).reciprocal
      // println("adding " + term1 + " - " + term2 + " to sum = " + sum)
      sum = sum.add(term1.subtract(term2))
    }

    // If n.real < 1, then instead of summing directly, we need to do the supersum calculation described in
    // https://www.youtube.com/watch?v=YuIIjLr6vUA&feature=emb_rel_pause about 17 - 18 minutes in.
    sum.divide(ComplexNumber(2.0 * n, 0))
  }
}
