/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.simulation.complexmapping.ComplexMappingTestAssertions
import org.scalatest.funsuite.AnyFunSuite

class IntPowerFunctionSuite extends AnyFunSuite with ComplexMappingTestAssertions {

  private val fun = IntPowerFunction()

  test("2^2 = 4") {
    assertComplexApprox(ComplexNumber(4, 0), fun.compute(ComplexNumber(2, 0), 2))
  }

  test("i^2 = -1") {
    assertComplexApprox(ComplexNumber(-1, 0), fun.compute(ComplexNumber(0, 1), 2))
  }

  test("(1+i)^2 = 2i") {
    assertComplexApprox(ComplexNumber(0, 2), fun.compute(ComplexNumber(1, 1), 2))
  }
}
