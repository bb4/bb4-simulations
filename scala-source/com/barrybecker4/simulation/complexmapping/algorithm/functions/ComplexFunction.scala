/* Copyright by Barry G. Becker, 2019-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm.functions

import com.barrybecker4.math.complex.ComplexNumber


trait ComplexFunction {

   /** @param s the complex valued parameter
     * @param n meaning depends on the function: an integer exponent (`IntPowerFunction`),
     *          an integer base (`PowerFunction`), an iteration/truncation count (ζ/η),
     *          or unused (`IdentityFunction`, `GammaFunction`)
     * @return the result of applying the function
     */
   def compute(s: ComplexNumber, n: Int): ComplexNumber
}
