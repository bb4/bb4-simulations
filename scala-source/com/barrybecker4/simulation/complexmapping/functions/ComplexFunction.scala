/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */

package com.barrybecker4.simulation.complexmapping.functions

import com.barrybecker4.common.math.ComplexNumber

trait ComplexFunction {
   def compute(v: ComplexNumber): ComplexNumber
}
