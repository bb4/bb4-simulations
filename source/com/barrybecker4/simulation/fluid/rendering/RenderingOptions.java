/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid.rendering;

import com.barrybecker4.common.concurrency.*;


/**
 * Fluid rendering options
 *
 * @author Barry Becker
 */
public class RenderingOptions {

    /** scales the size of everything   */
    private static final double DEFAULT_SCALE = 4;
    private double scale = DEFAULT_SCALE;

    private boolean showVelocities = false;
    private boolean showPressures = true;
    private boolean useLinearInterpolation = false;
    private boolean showGrid = false;

    /** Manages the worker threads. */
    private RunnableParallelizer parallelizer;


    /**
     * Constructor
     */
    public RenderingOptions() {
        setParallelized(false);
        setScale(DEFAULT_SCALE);
    }

    public void setParallelized(boolean useParallelization) {

        parallelizer =
             useParallelization ? new RunnableParallelizer() : new RunnableParallelizer(1);
    }

    public boolean isParallelized() {
        return parallelizer.getNumThreads() > 1;
    }

    RunnableParallelizer getParallelizer() {
        return parallelizer;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public void setShowVelocities(boolean show) {
        showVelocities = show;
    }

    public boolean getShowVelocities() {
        return showVelocities;
    }

    public void setShowPressures(boolean show) {
        showPressures = show;
    }

    public boolean getShowPressures() {
        return showPressures;
    }

    public void setUseLinearInterpolation(boolean useInterp) {
        useLinearInterpolation = useInterp;
    }
    public boolean getUseLinearInterpolation() {
        return useLinearInterpolation;
    }

    public boolean getShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }
}
