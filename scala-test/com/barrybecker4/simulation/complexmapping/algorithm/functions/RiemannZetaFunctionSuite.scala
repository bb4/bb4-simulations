/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.common.math.ComplexNumber
import org.scalatest.FunSuite

class RiemannZetaFunctionSuite extends FunSuite {

  private val fun = RiemannZetaFunction()

  test("zeta(2) with n=100") {
    assertResult(ComplexNumber(1, 0)) {
      fun.compute(ComplexNumber(2.0, 0), 100)
    }
  }

  test("zeta(1) with n=1") {

    assertResult(ComplexNumber(1, 0)) {
      fun.compute(ComplexNumber(1.0, 0), 1)
    }
  }

  test("zeta(0) with n=100 should be -1/2, but diverges, so accept NaN") {
    val result= fun.compute(ComplexNumber(0, 0), 100)
    assert(result.isNaN)
  }

  test("zeta(-1) with n=100 should be -1/12, but since it diverges, we accept NaN") {
    val result = fun.compute(ComplexNumber(-1, 0), 100)
    assert(result.isNaN)
  }

  test("zeta(1, i) with n=1") {
    assertResult(ComplexNumber(1, 0)) {
      fun.compute(ComplexNumber(1.0, 1.0), 1)
    }
  }

  test("zeta(1, i) with n=2") {
    assertResult(ComplexNumber(1.384619450681986, -0.3194806381568174)) {
      fun.compute(ComplexNumber(1.0, 1.0), 2)
    }
  }

  test("zeta(2, 3i) with n=3") {
    assertResult(ComplexNumber(0.7684594036038668, -0.20128090061917603)) {
      fun.compute(ComplexNumber(2.0, 3.0), 3)
    }
  }

  test("zeta(2, 3i) with n=300") {
    assertResult(ComplexNumber(0.7970905940475872, -0.11423413906585442)) {
      fun.compute(ComplexNumber(2.0, 3.0), 300)
    }
  }

  test("zeta(-1, i) with n=2") {
    val result = fun.compute(ComplexNumber(-1.0, 1.0), 2)
    assert(result.isNaN)
  }
}
