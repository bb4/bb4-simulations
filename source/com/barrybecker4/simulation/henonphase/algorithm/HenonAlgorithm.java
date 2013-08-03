/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.henonphase.algorithm;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.common.Profiler;

import java.awt.image.BufferedImage;

/**
 * Abstract implementation common to all Henon Phase algorithms.
 * Uses concurrency when parallelized is set.
 * This will give good speedup on multi-core machines.
 *
 * @author Barry Becker
 */
public class HenonAlgorithm {

    public static final int DEFAULT_MAX_ITERATIONS = 1000;
    public static final int DEFAULT_FRAME_ITERATIONS = 10;
    public static final int DEFAULT_NUM_TRAVELERS = 400;
    private static final int DEFAULT_SIZE = 300;
    private static final int DEFAULT_ALPHA = 200;
    private static final boolean DEFAULT_UNIFORM_SEEDS = true;
    private static final boolean DEFAULT_CONNECT_POINTS = false;


    private HenonModel model;

    // should extract these into ModelParams class
    private int numTravelors;
    private int maxIterations_;
    private int numStepsPerFrame;
    private TravelerParams travelorParams;
    private boolean useUniformSeeds;
    private boolean connectPoints;
    private int alpha;

    private ColorMap cmap;

    private boolean restartRequested = false;
    private boolean finished = false;
    private int iterations_ = 0;


    public HenonAlgorithm() {
        reset();
    }

    public void setSize(int width, int height)  {

        if (width != model.getWidth() || height != model.getHeight())   {
            requestRestart(width, height);
        }
    }

    public void reset() {
        numTravelors = DEFAULT_NUM_TRAVELERS;
        maxIterations_ = DEFAULT_MAX_ITERATIONS;
        numStepsPerFrame = DEFAULT_FRAME_ITERATIONS;
        travelorParams = new TravelerParams();
        useUniformSeeds = DEFAULT_UNIFORM_SEEDS;
        connectPoints = DEFAULT_CONNECT_POINTS;
        alpha = DEFAULT_ALPHA;
        cmap = new HenonColorMap(alpha);
        model = new HenonModel(DEFAULT_SIZE, DEFAULT_SIZE, travelorParams, useUniformSeeds, connectPoints, numTravelors, cmap);
    }

    public void setTravelerParams(TravelerParams newParams) {
        if (!newParams.equals(travelorParams))   {
            travelorParams = newParams;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    public void setAlpha(int newAlpha) {
        if (newAlpha != alpha) {
            alpha = newAlpha;
            cmap = new HenonColorMap(newAlpha);
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    public boolean getUseUniformSeeds() {
        return useUniformSeeds;
    }

    public void toggleUseUniformSeeds() {
        useUniformSeeds = !useUniformSeeds;
        requestRestart(model.getWidth(), model.getHeight());
    }

    public boolean getConnectPoints() {
        return connectPoints;
    }

    public void toggleConnectPoints() {
        connectPoints = !connectPoints;
        requestRestart(model.getWidth(), model.getHeight());
    }

    public void setNumTravelors(int newNumTravelors) {
        if (newNumTravelors != numTravelors)  {
            numTravelors= newNumTravelors;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    public void setMaxIterations(int value) {
        if (value != maxIterations_)  {
            maxIterations_ = value;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    public void setStepsPerFrame(int numSteps) {
        if (numSteps != numStepsPerFrame)
        {
            numStepsPerFrame = numSteps;
            requestRestart(model.getWidth(), model.getHeight());
        }
    }

    public ColorMap getColorMap() {
        return cmap;
    }

    private void requestRestart(int width, int height) {
        model = new HenonModel(width, height, travelorParams, useUniformSeeds, connectPoints, numTravelors, cmap);
        restartRequested = true;
    }

    public BufferedImage getImage() {
        return model.getImage();
    }

    /**
     * @param timeStep number of rows to compute on this timestep.
     * @return true when done computing whole model.
     */
    public boolean timeStep(double timeStep) {

        if (restartRequested) {
            restartRequested = false;
            finished = false;
            iterations_ = 0;
            model.reset();
            Profiler.getInstance().startCalculationTime();
        }
        if (iterations_ > maxIterations_) {
            showProfileInfo();
            return true;  // we are done.
        }

        model.increment(numStepsPerFrame);
        iterations_ += numStepsPerFrame;

        return false;
    }

    private void showProfileInfo() {
        if (!finished) {
            finished = true;
            Profiler prof = Profiler.getInstance();
            prof.stopCalculationTime();
            prof.print();
            prof.resetAll();
        }
    }
}
