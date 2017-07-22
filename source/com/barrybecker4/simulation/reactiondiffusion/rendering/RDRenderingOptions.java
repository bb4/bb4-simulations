/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering;

import com.barrybecker4.common.concurrency.RunnableParallelizer;

/**
 * Date: Aug 15, 2010
 *
 * @author Barry Becker
 */
public class RDRenderingOptions {

    private boolean isShowingU = false;
    private boolean isShowingV = true;

    /** used for scaling the bump height. if 0, then no bumpiness. */
    private double heightScale = 0;

    /** Specular highlight degree. */
    private double specularConst = 0;

    /** Manages the worker threads. */
    private RunnableParallelizer parallelizer;


    /**
     * Constructor
     */
    public RDRenderingOptions() {
        setParallelized(false);
    }

    public void setParallelized(boolean useParallelization) {

        parallelizer =
             useParallelization ? new RunnableParallelizer() : new RunnableParallelizer(1);
    }

    RunnableParallelizer getParallelizer() {
        return parallelizer;
    }

    public boolean isParallelized() {
        return parallelizer.getNumThreads() > 1;
    }

    public void setHeightScale(double h) {
        heightScale = h;
    }

    protected double getHeightScale() {
        return heightScale;
    }


    public void setSpecular(double s) {
        specularConst = s;
    }

    public double getSpecular() {
        return specularConst;
    }

    public boolean isShowingU() {
        return isShowingU;
    }

    public void setShowingU(boolean showingU) {
        isShowingU = showingU;
    }


    public boolean isShowingV() {
        return isShowingV;
    }

    public void setShowingV(boolean showingV) {
        isShowingV = showingV;
    }
}
