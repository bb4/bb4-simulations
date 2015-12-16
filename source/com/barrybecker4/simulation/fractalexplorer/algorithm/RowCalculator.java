/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm;

import com.barrybecker4.common.math.ComplexNumber;


/**
 * Calculate fractal values for a row.
 * Use run magnitude optimization if specified.
 *
 * @author Barry Becker
 */
public class RowCalculator {

    /** max run magnitude before we start skipping values. */
    private static final int M = 4;

    /** Amount to skip until we find a mismatch, then back up this amount. */
    private static final int N = 7;

    /** Amount to skip until we find a mismatch, then back up this amount. */
    private static final int HALF_N = 7;

    private FractalAlgorithm algorithm;
    private FractalModel model;

    private boolean useRunLengthOpt_;


    public RowCalculator(FractalAlgorithm algorithm) {
        this.algorithm = algorithm;
        this.model = algorithm.getModel();
    }


    public boolean getUseRunLengthOptimization() {
        return useRunLengthOpt_;
    }

    public void setUseRunLengthOptimization(boolean value) {
        if (useRunLengthOpt_ != value) {
            useRunLengthOpt_ = value;
            model.setCurrentRow(0);
        }
    }

    /**
     * Computes values for a row.
     */
    public void calculateRow(int width, int y) {

        if (useRunLengthOpt_) {
            calculateRowOptimized(width, y);
        }
        else {
            calculateRowSimple(width, y);
        }
    }

    /**
     * Computes values for a row.
     */
    private void calculateRowSimple(int width, int y) {

        for (int x = 0; x < width; x++)   {
            computeFractalValueForPosition(x, y);
        }
    }

    /**
     * Computes values for a row.
     * Take advantage of run lengths if turned on.
     * If using the run magnitude optimization, the results may not be absolutely perfect,
     * but anomalies should be very rare.
     * This can give 2x or more speedup when the MAX ITERATIONS is reasonably high.
     *
     * The run magnitude algorithm works like this.
     * March along until we find a run of M identical values. From that point evaluate only every Nth pixel
     * until we find a different value.
     * When that happens, back up N  pixels and evaluate each one again.
     * Need something to avoid skipping the tips of long thin triangular sections.
     */
    private void calculateRowOptimized(int width, int y) {

        int runLength = 0;
        int increment = 1;
        double lastValue = 0;

        int x = 0;
        while (x < width)  {
            double currentValue = computeFractalValueForPosition(x, y);

            if (lastValue == currentValue) {
                runLength += increment;
                /*
                if (increment == N) {
                    // make sure w do not skip over a narrow peninsula
                    double lastRowValue1 = model.getValue(x - HALF_N, y-1);
                    double lastRowValue2 = model.getValue(x - 2, y-1);
                    if (currentValue == 1.0)
                    System.out.println("currentValue=" + currentValue +  " lastRowValue=" + lastRowValue1 + " runLength=" + runLength + " increment=" + increment + " x="+ x);
                    if (currentValue != lastRowValue1 || currentValue != lastRowValue2 ) {
                        x -= (N-1);
                        currentValue = computeFractalValueForPosition(x, y);
                        increment = 1;
                        runLength = 0;
                    }
                }*/
            }
            else {
                lastValue = currentValue;
                if (runLength > M) {
                    x -= (N-1);
                    currentValue = computeFractalValueForPosition(x, y);
                    increment = 1;
                }
                runLength = 0;
            }

            if (runLength > M) {
                increment = N;
                for (int xx = x; xx < x+N; xx++) {
                    model.setValue(xx, y, currentValue);
                }
            }
            model.setValue(x, y, currentValue);
            x += increment;
        }
    }

    /**
     * @return the fractal value that was set into the model.
     */
    private double computeFractalValueForPosition(int x, int y) {
        ComplexNumber z = algorithm.getComplexPosition(x, y);
        double value = algorithm.getFractalValue(z);
        model.setValue(x, y, value);
        return value;
    }
}
