/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.common.math.ComplexNumber
import org.scalatest.FunSuite


// This is the analytic extension of the zeta function
class DirichletEtaFunctionSuite extends FunSuite {

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
/*
  test("eta(-3) with n=100 should be -1/120") {
    assertResult(ComplexNumber(-1/120, 0)) {
      fun.compute(ComplexNumber(-3, 0), 100)
    }
  }

  test("eta(-4) with n=100 should be -0") {
    assertResult(ComplexNumber(0, 0)) {
      fun.compute(ComplexNumber(-4, 0), 100)
    }
  }

  test("eta(1, i) with n=2") {
    assertResult(ComplexNumber(1.384619450681986, -0.3194806381568174)) {
      fun.compute(ComplexNumber(1.0, 1.0), 2)
    }
  }

  test("eta(2, 3i) with n=3") {
    assertResult(ComplexNumber(0.7684594036038668, -0.20128090061917603)) {
      fun.compute(ComplexNumber(2.0, 3.0), 3)
    }
  }

  test("eta(2, 3i) with n=300") {
    assertResult(ComplexNumber(0.798021168058162, -0.11374364531522344)) {
      fun.compute(ComplexNumber(2.0, 3.0), 300)
    }
  }

  test("eta(-1, i) with n=2") {
    assertResult(ComplexNumber(0.5804885123387991, -0.5077930715422768)) {
      fun.compute(ComplexNumber(-1.0, 1.0), 2)
    }
  }*/
}
