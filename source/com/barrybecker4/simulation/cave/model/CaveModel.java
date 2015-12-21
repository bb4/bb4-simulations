// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model;

import com.barrybecker4.simulation.cave.rendering.CaveRenderer;
import com.barrybecker4.simulation.common.Profiler;

import javax.swing.*;
import java.awt.image.BufferedImage;
import static com.barrybecker4.simulation.cave.model.CaveMap.DEFAULT_DENSITY;
import static com.barrybecker4.simulation.cave.model.CaveMap.DEFAULT_BIRTH_THRESHOLD;
import static com.barrybecker4.simulation.cave.model.CaveMap.DEFAULT_STARVATION_LIMIT;

/**
 * @author Barry Becker
 */
public class CaveModel {

    public static final int DEFAULT_MAX_ITERATIONS = 2;
    public static final double DEFAULT_SCALE = 5.0;

    //private static final int DEFAULT_CAVE_WIDTH = 50;
    //private static final int DEFAULT_CAVE_HEIGHT = 50;

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;


    private CaveMap cave;
    private CaveRenderer renderer;

    private double density = DEFAULT_DENSITY;
    private int maxIterations = DEFAULT_MAX_ITERATIONS;
    private int numIterations = 0;
    private int birthThresh = DEFAULT_BIRTH_THRESHOLD;
    private int starvationLimit = DEFAULT_STARVATION_LIMIT;
    private double scale = DEFAULT_SCALE;


    private boolean restartRequested = false;


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
        //numIterations = DEFAULT_NUM_ITERATIONS;
        int caveWidth = (int)(DEFAULT_WIDTH / scale);
        int caveHeight = (int)(DEFAULT_HEIGHT / scale);
        CaveMap cave = new CaveMap(caveWidth, caveHeight, density, starvationLimit, birthThresh);
        renderer = new CaveRenderer(DEFAULT_WIDTH, DEFAULT_HEIGHT, cave);
    }

    public void setMaxIterations(int num) {
        if (num != this.maxIterations) {
            maxIterations = num;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    public void setDensity(double density) {
        if (this.density != density)  {
            this.density = density;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    public void setBirthThreshold(int birthThresh) {
        if (this.birthThresh != birthThresh)  {
            this.birthThresh = birthThresh;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    public void setStarvationLimit(int starvationLimit) {
        if (this.starvationLimit != starvationLimit)  {
            this.starvationLimit = starvationLimit;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    public void setScale(double scale) {
        if (this.scale != scale)  {
            this.scale = scale;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    private void requestRestart(int width, int height) {
        try {
            int caveWidth = (int)(width / scale);
            int caveHeight = (int)(height / scale);
            cave = new CaveMap(caveWidth, caveHeight, density, starvationLimit, birthThresh);
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
            renderer.reset();
            Profiler.getInstance().startCalculationTime();
            renderer.render();
        }
        else if (numIterations < maxIterations) {
            cave.nextPhase();
            numIterations++;
            renderer.render();
        }

        return false;
    }
}
