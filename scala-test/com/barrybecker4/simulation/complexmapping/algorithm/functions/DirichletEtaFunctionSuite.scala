/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.simulation.complexmapping.ComplexMappingTestAssertions
import org.scalatest.funsuite.AnyFunSuite

class DirichletEtaFunctionSuite extends AnyFunSuite with ComplexMappingTestAssertions {

  private val fun = DirichletEtaFunction()

  test("eta(2) with n=200 approaches pi^2/12") {
    assertComplexApprox(
      ComplexNumber(math.Pi * math.Pi / 12.0, 0),
      fun.compute(ComplexNumber(2.0, 0), 200),
      tol = 1e-4
    )
  }

  test("eta(1) with n=5000 approaches ln(2)") {
    assertComplexApprox(
      ComplexNumber(math.log(2.0), 0),
      fun.compute(ComplexNumber(1.0, 0), 5000),
      tol = 1e-3
    )
  }

  test("eta(0.5) with n=20000 approaches known value in critical strip") {
    // Alternating series at Re=0.5 has terms ~k^{-1/2}; truncation dominates tol.
    assertComplexApprox(
      ComplexNumber(0.6048986434, 0),
      fun.compute(ComplexNumber(0.5, 0), 20000),
      tol = 5e-3
    )
  }

  test("eta(0) with n=400 approaches 1/2") {
    assertComplexApprox(
      ComplexNumber(0.5, 0),
      fun.compute(ComplexNumber(0, 0), 400),
      tol = 1e-3
    )
  }

  test("eta(-1) with n=2000 approaches 1/4") {
    assertComplexApprox(
      ComplexNumber(0.25, 0),
      fun.compute(ComplexNumber(-1, 0), 2000),
      tol = 2e-3
    )
  }
}
