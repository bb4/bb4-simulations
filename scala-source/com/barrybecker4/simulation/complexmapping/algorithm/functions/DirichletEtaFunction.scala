/* Copyright by Barry G. Becker, 2019-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber
import DirichletEtaFunction.MAX_HOLDER_ORDER


/**
  * Dirichlet eta function η(s) = Σ (-1)^{k-1} k^{-s}.
  * Converges for Re(s) > 0. For Re(s) ≤ 0, uses Hölder (Cesàro) means of the partial sums.
  * Relation to zeta: ζ(s) = η(s) / (1 − 2^{1−s}) for s ≠ 1.
  * See https://www.youtube.com/watch?v=YuIIjLr6vUA
  */
case class DirichletEtaFunction() extends ComplexFunction {

  /** @param s complex number
    * @param n the number of iterations. The larger this is, the more accurate the result.
    * @return the result of applying the function
    */
  override def compute(s: ComplexNumber, n: Int): ComplexNumber = {
    if (n < 1) ComplexNumber(0)
    else if (s.real > 0.0) ordinaryPartialSum(s, n)
    else holderMean(s, n)
  }

  /** When Re(s) > 0 the alternating series converges (conditionally for Re ≤ 1). */
  private def ordinaryPartialSum(s: ComplexNumber, n: Int): ComplexNumber = {
    var sum = ComplexNumber(0, 0)
    for (k <- 1 to n) {
      val term = ComplexNumber.pow(k.toDouble, s).reciprocal
      sum = if (k % 2 == 1) sum.add(term) else sum.subtract(term)
    }
    sum
  }

  /**
    * Hölder mean of order m of the alternating partial sums.
    * The Dirichlet series is (C, k) summable for Re(s) > −k.
    * Order is min(8, max(1, floor(−Re(s)) + 1)).
    */
  private def holderMean(s: ComplexNumber, n: Int): ComplexNumber = {
    val order = math.min(MAX_HOLDER_ORDER, math.max(1, math.floor(-s.real).toInt + 1))
    val partials = new Array[ComplexNumber](n)
    var sum = ComplexNumber(0)
    for (k <- 1 to n) {
      val term = ComplexNumber.pow(k.toDouble, s).reciprocal
      sum = if (k % 2 == 1) sum.add(term) else sum.subtract(term)
      partials(k - 1) = sum
    }

    var seq = partials
    for (_ <- 1 to order) {
      val next = new Array[ComplexNumber](seq.length)
      var prefix = ComplexNumber(0)
      for (i <- seq.indices) {
        prefix = prefix.add(seq(i))
        next(i) = prefix.divide(ComplexNumber((i + 1).toDouble))
      }
      seq = next
    }
    seq(seq.length - 1)
  }
}

object DirichletEtaFunction {
  private val MAX_HOLDER_ORDER = 8
}
