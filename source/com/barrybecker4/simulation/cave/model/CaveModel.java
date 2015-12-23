// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model;

import com.barrybecker4.simulation.cave.rendering.CaveRenderer;
import com.barrybecker4.simulation.common.Profiler;

import javax.swing.*;
import java.awt.image.BufferedImage;
import static com.barrybecker4.simulation.cave.model.CaveProcessor.DEFAULT_DENSITY;
import static com.barrybecker4.simulation.cave.model.CaveProcessor.DEFAULT_BIRTH_THRESHOLD;
import static com.barrybecker4.simulation.cave.model.CaveProcessor.DEFAULT_STARVATION_LIMIT;

/**
 * @author Barry Becker
 */
public class CaveModel {

    public static final int DEFAULT_MAX_ITERATIONS = 2;
    public static final double DEFAULT_SCALE = 5.0;

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    private CaveProcessor cave;
    private CaveRenderer renderer;

    private double density = DEFAULT_DENSITY;
    //private int maxIterations = DEFAULT_MAX_ITERATIONS;
    private int numIterations = 0;
    private int birthThresh = DEFAULT_BIRTH_THRESHOLD;
    private int starvationLimit = DEFAULT_STARVATION_LIMIT;
    private double scale = DEFAULT_SCALE;
    private CaveProcessor.KernelType kernalType = CaveProcessor.DEFAULT_KERNEL_TYPE;

    private boolean restartRequested = false;
    private boolean nextStepRequested = false;


    public CaveModel() {
        reset();
    }

    public void setSize(int width, int height)  {
        if (width != renderer.getWidth() || height != renderer.getHeight())   {
            requestRestart(width, height);
        }
    }

    public void reset() {
        density = DEFAULT_DENSITY;
        int caveWidth = (int)(DEFAULT_WIDTH / scale);
        int caveHeight = (int)(DEFAULT_HEIGHT / scale);
        CaveProcessor cave = new CaveProcessor(caveWidth, caveHeight, density, starvationLimit, birthThresh, kernalType);
        renderer = new CaveRenderer(DEFAULT_WIDTH, DEFAULT_HEIGHT, cave);
    }

    /*
    public void setMaxIterations(int num) {
        if (num != this.maxIterations) {
            maxIterations = num;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    } */

    public void setDensity(double density) {
        this.density = density;
    }

    public void setBirthThreshold(int birthThresh) {
        cave.setBirthThreshold(birthThresh);
        this.birthThresh = birthThresh;
    }

    public void setStarvationLimit(int starvationLimit) {
        cave.setStarvationLimit(starvationLimit);
        this.starvationLimit = starvationLimit;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void requestRestart() {
        requestRestart(renderer.getWidth(), renderer.getHeight());
    }

    public void requestNextStep() {
        nextStepRequested = true;
    }

    public void setKernelType(CaveProcessor.KernelType type) {
        cave.setKernelType(type);
        this.kernalType = type;
        //requestRestart(renderer.getWidth(), renderer.getHeight());
    }

    private void requestRestart(int width, int height) {
        try {
            int caveWidth = (int)(width / scale);
            int caveHeight = (int)(height / scale);
            cave = new CaveProcessor(caveWidth, caveHeight, density, starvationLimit, birthThresh, kernalType);
            numIterations = 0;
            renderer = new CaveRenderer(width, height, cave);
            restartRequested = true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public BufferedImage getImage() {
        return renderer.getImage();
    }

    /**
     * @param timeStep number of rows to compute on this timestep.
     * @return true when done computing whole renderer.
     */
    public boolean timeStep(double timeStep) {

        if (restartRequested) {
            restartRequested = false;
            nextStepRequested = false;
            numIterations = 0;
            Profiler.getInstance().startCalculationTime();
            renderer.render();
        }
        else if (nextStepRequested/*numIterations < maxIterations*/) {
            cave.nextPhase();
            numIterations++;
            renderer.render();
            nextStepRequested = false;
        }

        return false;
    }
}
