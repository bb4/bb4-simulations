/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.ComplexNumber


/**
  * This function extends the zeta function so its domain is the whole complex plane.
  * See http://tmajlath.byethost13.com/Riemann.html?ckattempt=1&i=1
  *
  * Also see 35 minutes into https://www.youtube.com/watch?v=YuIIjLr6vUA&feature=emb_rel_pause
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
  * All the non-trivial zeroes are hypothesized (by Riemann) to lie on z = 1/2
  *
  * The values at 0 and 1 are special and are computed according to
  * https://en.wikipedia.org/wiki/Particular_values_of_the_Riemann_zeta_function
  */
case class ZetaAnalyticExtensionFunction() extends ComplexFunction {

  val zetaFunction = RiemannZetaFunction()
  val etaFun = DirichletEtaFunction()
  val gammaFunction = GammaFunction()

  /** @param s complex number
    * @param n the number of iterations. The larger this is, the more accurate the result.
    * @return the result of applying the function
    */
  override def compute(s: ComplexNumber, n: Int): ComplexNumber = {

    val result =
      if (s.real == 1) ComplexNumber(Double.PositiveInfinity, 0)
      else if (s.real == 0) ComplexNumber(-0.5, 0)
      else if (s.real > 1) zetaFunction.compute(s, n)
      else if (s.real > 0) continuanceInCriticalRegion(s, n)
      else continuanceInNegativeRegion(s, n)

    if (result.real.isInfinite || result.real.isNaN || result.imaginary.isNaN) ComplexNumber(0) else result
  }

  private def continuanceInCriticalRegion(s: ComplexNumber, n: Int): ComplexNumber = {
    val num = etaFun.compute(s, n)
    val denominator = ComplexNumber(1.0, 0).subtract(ComplexNumber(2.0, 0).divide(ComplexNumber.pow(2.0, s)))
    num.divide(denominator)
  }

  /** ζ(s) = 2(2π&caret;-s')cos(s'π/2)Γ(s')ζ(s') where s' = 1 - s  (the web ref has s - 1, but I think that is a typo)
   *  also see http://www.nhn.ou.edu/~milton/p5013/zeta.pdf
   */
  private def continuanceInNegativeRegion(s: ComplexNumber, n: Int): ComplexNumber = {
    val sp = ComplexNumber(1).subtract(s)
    val term1 = ComplexNumber.pow(Math.PI * 2.0, sp.negate).multiply(2.0)
    val cosTerm = ComplexNumber.cos(sp.multiply(Math.PI / 2.0))
    val gammaTerm = gammaFunction.compute(sp, n)
    val zetaTerm = zetaFunction.compute(sp, n)
    term1.multiply(cosTerm).multiply(gammaTerm).multiply(zetaTerm)
  }
}
