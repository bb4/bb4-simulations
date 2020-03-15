/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber


case class IdentityFunction() extends ComplexFunction {

  override def compute(v: ComplexNumber, n: Int): ComplexNumber = v
}
