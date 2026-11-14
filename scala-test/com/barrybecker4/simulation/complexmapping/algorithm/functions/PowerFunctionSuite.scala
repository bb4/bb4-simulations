/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.simulation.complexmapping.ComplexMappingTestAssertions
import org.scalatest.funsuite.AnyFunSuite

class PowerFunctionSuite extends AnyFunSuite with ComplexMappingTestAssertions {

  private val fun = PowerFunction()

  test("2^2 = 4") {
    assertComplexApprox(ComplexNumber(4, 0), fun.compute(ComplexNumber(2, 0), 2))
  }

  test("2^i is finite and non-trivial") {
    val result = fun.compute(ComplexNumber(0, 1), 2)
    assert(!(result.real.isNaN || result.imaginary.isNaN))
    // 2^i = e^{i ln 2} = cos(ln 2) + i sin(ln 2)
    assertComplexApprox(
      ComplexNumber(math.cos(math.log(2)), math.sin(math.log(2))),
      result,
      tol = 1e-10
    )
  }
}
