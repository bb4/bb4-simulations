/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber


/**
  * Analytic continuation of the Riemann zeta function to the whole complex plane.
  * See http://tmajlath.byethost13.com/Riemann.html?ckattempt=1&i=1
  *
  * Relation: ζ(z) = η(z) / (1 − 2^{1−z}) for z ≠ 1.
  * The only pole is at z = 1.
  *
  * Particular values:
  * ζ(0) = −1/2, ζ(−1) = −1/12, ζ(−2) = 0, ζ(−3) = 1/120, ζ(−4) = 0.
  * Trivial zeros at z = −2, −4, −6, …
  *
  * Visualization maps non-finite results (including the pole) to 0.
  * See https://en.wikipedia.org/wiki/Particular_values_of_the_Riemann_zeta_function
  */
case class ZetaAnalyticExtensionFunction() extends ComplexFunction {

  private lazy val zetaFunction = RiemannZetaFunction()
  private lazy val etaFun = DirichletEtaFunction()
  private lazy val gammaFunction = GammaFunction()

  /** @param s complex number
    * @param n the number of iterations. The larger this is, the more accurate the result.
    * @return the result of applying the function
    */
  override def compute(s: ComplexNumber, n: Int): ComplexNumber = {

    val result =
      if (s.imaginary == 0.0 && s.real == 1.0) ComplexNumber(Double.PositiveInfinity)
      else if (s.imaginary == 0.0 && s.real == 0.0) ComplexNumber(-0.5)
      else if (s.real > 1) zetaFunction.compute(s, n)
      else if (s.real < 0) continuanceInNegativeRegion(s, n)
      else continuanceInCriticalRegion(s, n) // critical strip and imag axis except origin

    // Visualization uses finite values only; poles / failed numerics become 0 (not zeta(1)=∞).
    if (result.real.isInfinite || result.real.isNaN || result.imaginary.isNaN) ComplexNumber(0) else result
  }

  private def continuanceInCriticalRegion(s: ComplexNumber, n: Int): ComplexNumber = {
    val num = etaFun.compute(s, n)
    val denominator = ComplexNumber(1.0, 0).subtract(ComplexNumber(2.0, 0).divide(ComplexNumber.pow(2.0, s)))
    num.divide(denominator)
  }

  /** ζ(s) = 2(2π)^{-s'}cos(s'π/2)Γ(s')ζ(s') where s' = 1 - s
   *  Equivalent to ζ(s) = 2^s π^{s−1} sin(πs/2) Γ(1−s) ζ(1−s).
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
