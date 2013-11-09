/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm;

import com.barrybecker4.common.math.ComplexNumber;
import com.barrybecker4.common.math.ComplexNumberRange;

/**
 * Populates the FractalModel using the iterative Mandelbrot algorithm..
 *
 * @author Barry Becker
 */
public class MandelbrotAlgorithm extends FractalAlgorithm  {

    private static final ComplexNumberRange INITIAL_RANGE =
            new ComplexNumberRange(new ComplexNumber(-2.1, -1.5), new ComplexNumber(1.1, 1.5));

    public MandelbrotAlgorithm(FractalModel model) {
        super(model, INITIAL_RANGE);
        model.setCurrentRow(0);
    }

    @Override
    public double getFractalValue(ComplexNumber initialValue) {

        ComplexNumber z = initialValue;
        int numIterations = 0;

        while (z.getMagnitude() < 2.0 && numIterations < getMaxIterations()) {
            z = z.power(2).add(initialValue);
            numIterations++;
        }

        return (double) numIterations / getMaxIterations();
    }

}
