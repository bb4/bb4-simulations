/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm;

import com.barrybecker4.common.math.ComplexNumber;
import com.barrybecker4.common.math.ComplexNumberRange;

/**
 * Populates the FractalModel using the iterative Julia set algorithm.
 *
 * @author Barry Becker
 */
public class JuliaAlgorithm extends FractalAlgorithm  {

    public static final ComplexNumber DEFAULT_JULIA_SEED = new ComplexNumber(0.233, 0.5378);

    private static final ComplexNumberRange INITIAL_RANGE =
            new ComplexNumberRange(new ComplexNumber(-1.8, -1.7), new ComplexNumber(1.8, 1.7));

    private ComplexNumber seed = DEFAULT_JULIA_SEED;

    public JuliaAlgorithm(FractalModel model) {
        super(model, INITIAL_RANGE);
        model.setCurrentRow(0);
    }

    public void setJuliaSeed(ComplexNumber seed) {
        System.out.println("setting jSeed to " + seed);
        this.seed = seed;
    }

    @Override
    public double getFractalValue(ComplexNumber initialValue) {

        ComplexNumber z = initialValue;
        int numIterations = 0;

        while (z.getMagnitude() < 2.0 && numIterations < getMaxIterations()) {
            z = z.power(2).add(seed);
            numIterations++;
        }

        return (double) numIterations / getMaxIterations();
    }

}
