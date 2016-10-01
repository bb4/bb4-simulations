/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering;

import com.barrybecker4.common.concurrency.RunnableParallelizer;

import javax.vecmath.Vector3d;

/**
 * Date: Aug 15, 2010
 *
 * @author Barry Becker
 */
public class RDRenderingOptions {

    private boolean isShowingU_ = false;
    private boolean isShowingV_ = true;

    /** used for scaling the bump height. if 0, then no bumpiness. */
    private double heightScale_ = 0;

    /** Specular highlight degree. */
    private double specularConst_ = 0;

    /** Manages the worker threads. */
    private RunnableParallelizer parallelizer_;


    /**
     * Constructor
     */
    public RDRenderingOptions() {
        setParallelized(false);
    }

    public void setParallelized(boolean useParallelization) {

        parallelizer_ =
             useParallelization ? new RunnableParallelizer() : new RunnableParallelizer(1);
    }

    public RunnableParallelizer getParallelizer() {
        return parallelizer_;
    }

    public boolean isParallelized() {
        return parallelizer_.getNumThreads() > 1;
    }

    public void setHeightScale(double h) {
        heightScale_ = h;
    }

    protected double getHeightScale() {
        return heightScale_;
    }


    public void setSpecular(double s) {
        specularConst_ = s;
    }

    public double getSpecular() {
        return specularConst_;
    }

    public boolean isShowingU() {
        return isShowingU_;
    }

    public void setShowingU(boolean showingU) {
        isShowingU_ = showingU;
    }


    public boolean isShowingV() {
        return isShowingV_;
    }

    public void setShowingV(boolean showingV) {
        isShowingV_ = showingV;
    }
}
