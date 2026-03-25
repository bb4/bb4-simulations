/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.simulation.complexmapping.ComplexMappingTestAssertions
import org.scalatest.funsuite.AnyFunSuite

class GammaFunctionSuite extends AnyFunSuite with ComplexMappingTestAssertions {

  private val gamma = GammaFunction()
  private val nDummy = 10 // iteration count is ignored by GammaFunction

  test("Gamma(1) is 1") {
    assertComplexApprox(ComplexNumber(1, 0), gamma.compute(ComplexNumber(1, 0), nDummy))
  }

  test("Gamma(2) is 1") {
    assertComplexApprox(ComplexNumber(1, 0), gamma.compute(ComplexNumber(2, 0), nDummy))
  }

  test("Gamma(3) is 2") {
    assertComplexApprox(ComplexNumber(2, 0), gamma.compute(ComplexNumber(3, 0), nDummy))
  }

  test("reflection: Gamma(0.25) * Gamma(0.75) = pi / sin(pi/4)") {
    val g025 = gamma.compute(ComplexNumber(0.25, 0), nDummy)
    val g075 = gamma.compute(ComplexNumber(0.75, 0), nDummy)
    val prod = g025.multiply(g075)
    val expected = math.Pi / math.sin(math.Pi * 0.25)
    assertComplexApprox(ComplexNumber(expected, 0), prod, tol = 1e-4)
  }
}
