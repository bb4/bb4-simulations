/*
 * Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.simulation.complexmapping.ComplexMappingTestAssertions
import org.scalatest.funsuite.AnyFunSuite

class ZetaAnalyticExtensionFunctionSuite extends AnyFunSuite with ComplexMappingTestAssertions {

  private val fun = ZetaAnalyticExtensionFunction()

  /** Alternating series for η, computed in-test so a shared eta bug cannot pass zeta tests. */
  private def etaAlternatingSum(s: ComplexNumber, n: Int): ComplexNumber = {
    var sum = ComplexNumber(0)
    for (k <- 1 to n) {
      val term = ComplexNumber.pow(k.toDouble, s).reciprocal
      sum = if (k % 2 == 1) sum.add(term) else sum.subtract(term)
    }
    sum
  }

  private def zetaFromEta(s: ComplexNumber, n: Int): ComplexNumber = {
    val num = etaAlternatingSum(s, n)
    val denom = ComplexNumber(1.0).subtract(ComplexNumber(2.0, 0).divide(ComplexNumber.pow(2.0, s)))
    num.divide(denom)
  }

  test("z(2) with n=1000 approaches pi^2/6") {
    assertComplexApprox(
      ComplexNumber(math.Pi * math.Pi / 6.0),
      fun.compute(ComplexNumber(2.0), 1000),
      tol = 2e-3
    )
  }

  test("z(0) is -1/2") {
    assertResult(ComplexNumber(-0.5)) {
      fun.compute(ComplexNumber(0), 100)
    }
  }

  test("z(1) maps pole to viz value 0") {
    assertResult(ComplexNumber(0)) {
      fun.compute(ComplexNumber(1.0), 1)
    }
  }

  test("z(-1) with n=1000 approaches -1/12") {
    assertComplexApprox(
      ComplexNumber(-1.0 / 12.0),
      fun.compute(ComplexNumber(-1), 1000),
      tol = 1e-4
    )
  }

  test("z(-2) with n=50 is a trivial zero") {
    assertComplexApprox(
      ComplexNumber(0),
      fun.compute(ComplexNumber(-2), 50),
      tol = 1e-8
    )
  }

  test("z(-3) with n=200 approaches +1/120") {
    assertComplexApprox(
      ComplexNumber(1.0 / 120.0),
      fun.compute(ComplexNumber(-3), 200),
      tol = 1e-5
    )
  }

  test("z(1+i) with n=400 is finite via eta relation") {
    val s = ComplexNumber(1.0, 1.0)
    val actual = fun.compute(s, 400)
    assert(!(actual.real.isNaN || actual.imaginary.isNaN), s"expected finite but got $actual")
    assert(actual != ComplexNumber(0), s"expected non-zero but got $actual")
    assertComplexApprox(zetaFromEta(s, 400), actual, tol = 1e-2)
  }

  test("z(0.5) with n=2000 matches eta continuance in critical strip") {
    val s = ComplexNumber(0.5)
    assertComplexApprox(zetaFromEta(s, 2000), fun.compute(s, 2000), tol = 1e-2)
  }
}
