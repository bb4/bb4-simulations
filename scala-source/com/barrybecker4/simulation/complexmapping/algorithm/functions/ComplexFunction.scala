/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.common.math.ComplexNumber


trait ComplexFunction {

   /** @param s the complex valued parameter
     * @param n an exponent that may or may not be used in the function
     * @return the result of applying the function
     */
   def compute(s: ComplexNumber, n: Int): ComplexNumber
}
