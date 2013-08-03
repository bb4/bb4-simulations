/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm;

import com.barrybecker4.common.concurrency.Parallelizer;
import com.barrybecker4.common.geometry.Box;
import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.math.ComplexNumber;
import com.barrybecker4.common.math.ComplexNumberRange;
import com.barrybecker4.simulation.common.Profiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation common to all fractal algorithms.
 * Uses concurrency when parallelized is set.
 * This will give good speedup on multi-core machines.
 *
 * For core 2 Duo:
 *  - not-parallel 32.4 seconds
 *  - parallel     19.5 seconds
 *
 *  For i7 2600k
 *     original                     after rendering optimization.
 *  - not-parallel  16.0  seconds         6.1
 *  - parallel       5.1 seconds          2.1
 * @author Barry Becker
 */
public abstract class FractalAlgorithm {

    public static final int DEFAULT_MAX_ITERATIONS = 500;

    protected final FractalModel model;

    /** range of bounding box in complex plane. */
    private ComplexNumberRange range;

    /** Manages the worker threads. */
    private Parallelizer<Worker> parallelizer_;

    private int maxIterations_ = DEFAULT_MAX_ITERATIONS;

    private RowCalculator rowCalculator_;

    private boolean restartRequested = false;
    private boolean wasDone = false;

    private History history_ = new History();


    public FractalAlgorithm(FractalModel model, ComplexNumberRange range) {
        this.model = model;
        setRange(range);
        setParallelized(true);
        rowCalculator_ = new RowCalculator(this);
    }

    public void setRange(ComplexNumberRange range)  {

        history_.addRangeToHistory(range);
        processRange(range);
    }

    /**
     * Back up one step in the history
     */
    public void goBack() {
        ComplexNumberRange newRange = history_.popLastRange();
        if (range.equals(newRange)) {
            newRange = history_.popLastRange();
        }
        processRange(newRange);
    }

    private void processRange(ComplexNumberRange range) {
        this.range = range;
        restartRequested = true;
    }

    public boolean isParallelized() {
        return (parallelizer_.getNumThreads() > 1);
    }

    public void setParallelized(boolean parallelized) {
        if (parallelizer_ == null || parallelized != isParallelized()) {

            parallelizer_ =
                 parallelized ? new Parallelizer<Worker>() : new Parallelizer<Worker>(1);
            model.setCurrentRow(0);
        }
    }

    public int getMaxIterations() {
        return maxIterations_;
    }

    public void setMaxIterations(int value) {
        if (value != maxIterations_)  {
            maxIterations_ = value;
            model.setCurrentRow(0);
        }
    }

    public boolean getUseRunLengthOptimization() {
        return rowCalculator_.getUseRunLengthOptimization();
    }

    public void setUseRunLengthOptimization(boolean value) {
        rowCalculator_.setUseRunLengthOptimization(value);
    }

    public FractalModel getModel() {
        return model;
    }

    /**
     * @param timeStep number of rows to compute on this timestep.
     * @return true when done computing whole model.
     */
    public boolean timeStep(double timeStep) {

        if (restartRequested) {
            model.setCurrentRow(0);
            restartRequested = false;
        }
        if (model.isDone()) {
            showProfileInfoWhenFinished();
            return true;  // we are done.
        }

        int numProcs = parallelizer_.getNumThreads();
        List<Runnable> workers = new ArrayList<Runnable>(numProcs);

        // we calculate a little more each "timestep"
        int currentRow = model.getCurrentRow();
        startProfileTimeIfNeeded(currentRow);

        int height = model.getHeight();
        int computeToRow = Math.min(height, currentRow + (int)timeStep * numProcs);

        int diff = computeToRow - currentRow;
        if (diff == 0) return true;

        int chunk = Math.max(1, diff / numProcs);

        for (int i = 0; i < numProcs; i++) {
            int nextRow = Math.min(height, currentRow + chunk);
            workers.add(new Worker(currentRow, nextRow));
            //System.out.println("creating worker ("+i+") to compute " + chunk +" rows.");
            currentRow = nextRow;
        }

        // blocks until all Callables are done running.
        parallelizer_.invokeAllRunnables(workers);

        model.setCurrentRow(currentRow);

        return false;
    }


    /**
     * @return a number between 0 and 1.
     * Typically corresponds to the number times we had to iterate before the point escaped (or not).
     */
    public abstract double getFractalValue(ComplexNumber seed);

    /**
     * Converts from screen coordinates to data coordinates.
     * @param x
     * @param y
     * @return corresponding position in complex number plane represented by the model.
     */
    public ComplexNumber getComplexPosition(int x, int y) {
        return range.getInterpolatedPosition((double)x / model.getWidth(), (double)y / model.getHeight());
    }

    public ComplexNumber getComplexPosition(IntLocation loc) {
        return getComplexPosition(loc.getX(), loc.getY());
    }

    public ComplexNumberRange getRange(Box box)  {
        ComplexNumber firstCorner = getComplexPosition(box.getTopLeftCorner());
        ComplexNumber secondCorner =getComplexPosition(box.getBottomRightCorner());
        return new ComplexNumberRange(firstCorner, secondCorner);
    }


    private void startProfileTimeIfNeeded(int currentRow) {
        if (currentRow == 0) {
            Profiler.getInstance().startCalculationTime();
            wasDone = false;
        }
    }

    private void showProfileInfoWhenFinished() {
        if (!wasDone) {
            Profiler prof = Profiler.getInstance();
            prof.stopCalculationTime();
            prof.print();
            prof.resetAll();
            wasDone = true;
        }
    }

    /**
     * Runs one of the chunks.
     */
    private class Worker implements Runnable {

        private int fromRow_;
        private int toRow_;

        public Worker(int fromRow, int toRow) {

            fromRow_ = fromRow;
            toRow_ = toRow;
        }

        public void run() {
            computeChunk(fromRow_, toRow_);
        }

        /**
         * Do a chunk of work (i.e. compute the specified rows)
         */
        private void computeChunk(int fromRow, int toRow) {

            int width = model.getWidth();
            for (int y = fromRow; y < toRow; y++)   {
                rowCalculator_.calculateRow(width, y);
            }
        }
    }
}
