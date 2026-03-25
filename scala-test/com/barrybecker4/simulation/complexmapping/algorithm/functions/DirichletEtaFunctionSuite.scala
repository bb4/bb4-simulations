/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber
import org.scalatest.funsuite.AnyFunSuite


// This is the analytic extension of the zeta function
class DirichletEtaFunctionSuite extends AnyFunSuite {

  private val fun = DirichletEtaFunction()

  test("eta(2) with n=1000") {
    assertResult(ComplexNumber(0.8224669084866097, 0)) {
      fun.compute(ComplexNumber(2.0, 0), 1000)
    }
  }

  test("eta(1) with n=1") {
    assertResult(ComplexNumber(0.25, 0)) {
      fun.compute(ComplexNumber(1.0, 0), 1)
    }
  }

  test("eta(0) with n=100 should be 0 or 1 (divergent)") {
    assertResult(ComplexNumber(0, 0)) {
      fun.compute(ComplexNumber(0, 0), 100)
    }
  }

  test("eta(1, i) with n=1") {
    assertResult(ComplexNumber(0.307690274659007, 0.1597403190784087)) {
      fun.compute(ComplexNumber(1.0, 1.0), 1)
    }
  }

  test("eta(-1) with n=100 should be -1/12") {
    assertResult(ComplexNumber(-0.5000000000000007, 0)) {
      fun.compute(ComplexNumber(-1, 0), 100)
    }
  }
}
