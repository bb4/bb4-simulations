/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping

import com.barrybecker4.math.complex.ComplexNumber
import org.scalatest.funsuite.AnyFunSuite

/** Shared assertions for complex-mapping numeric tests. */
trait ComplexMappingTestAssertions { this: AnyFunSuite =>

  def assertComplexApprox(expected: ComplexNumber, actual: ComplexNumber, tol: Double = 1e-6): Unit =
    assert(
      math.abs(expected.real - actual.real) <= tol && math.abs(expected.imaginary - actual.imaginary) <= tol,
      s"expected $expected but got $actual"
    )
}
