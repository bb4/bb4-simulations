/*
 * Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.ComplexNumber
import org.scalatest.FunSuite


// This is the analytic extension of the zeta function
class ZetaAnalyticExtensionFunctionSuite extends FunSuite {

  private val fun = ZetaAnalyticExtensionFunction()

  test("z(2) with n=1000") {
    assertResult(ComplexNumber(1.6349839001848923, 0)) {
      fun.compute(ComplexNumber(2.0), 100)
    }
  }

  test("z(1) with n=1") {
    assertResult(ComplexNumber(0)) {
      fun.compute(ComplexNumber(1.0), 1)
    }
  }

  test("z(0) with n=100 should be 1/2") {
    assertResult(ComplexNumber(-0.5)) {
      fun.compute(ComplexNumber(0), 100)
    }
  }

  test("z(1, i) with n=1") {
    assertResult(ComplexNumber(0)) {
      fun.compute(ComplexNumber(1.0, 1.0), 1)
    }
  }

  test("z(-1) with n=100 should be -1/12") {
    assertResult(ComplexNumber(-0.08282925200144968, 0)) {  // this should be about -1/12
      fun.compute(ComplexNumber(-1), 100)
    }
  }

  test("z(-1) with n=1000 should be -1/12") {
    assertResult(ComplexNumber(-0.08328269806336484, 0)) {  // this should be -1/12 = -0.08333333333333
      fun.compute(ComplexNumber(-1, 0), 1000)
    }
  }

  test("z(-3) with n=100 should be -1/120") {
    assertResult(ComplexNumber(0.008333330805078558, 0)) {  // should be -1/120
      fun.compute(ComplexNumber(-3, 0), 100)
    }
  }

  test("z(-4) with n=100 should be -0") {
    assertResult(ComplexNumber(1.5561134419018175E-18, 0)) {  // should be 0
      fun.compute(ComplexNumber(-4, 0), 100)
    }
  }

  test("z(1, i) with n=2") {
    assertResult(ComplexNumber(0)) {
      fun.compute(ComplexNumber(1.0, 1.0), 2)
    }
  }

  test("z(2, 3i) with n=3") {
    assertResult(ComplexNumber(0.7684594036038668, -0.20128090061917603)) {
      fun.compute(ComplexNumber(2.0, 3.0), 3)
    }
  }

  test("z(2, 3i) with n=300") {
    assertResult(ComplexNumber(0.7970905940475872, -0.11423413906585442)) {
      fun.compute(ComplexNumber(2.0, 3.0), 300)
    }
  }

  test("z(-1, i) with n=2") {
    assertResult(ComplexNumber(-0.009406554678370524, -0.11239864467936167)) {
      fun.compute(ComplexNumber(-1.0, 1.0), 2)
    }
  }
}
