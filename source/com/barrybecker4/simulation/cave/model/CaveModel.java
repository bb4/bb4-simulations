// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model;

import com.barrybecker4.simulation.cave.rendering.CaveRenderer;
import com.barrybecker4.simulation.common.Profiler;

import javax.swing.*;
import java.awt.image.BufferedImage;
import static com.barrybecker4.simulation.cave.model.CaveMap.DEFAULT_DENSITY;

/**
 * @author Barry Becker
 */
public class CaveModel {

    //private static final int DEFAULT_NUM_ITERATIONS = 2;

    private static final int DEFAULT_CAVE_WIDTH = 50;
    private static final int DEFAULT_CAVE_HEIGHT = 50;

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    private CaveRenderer renderer;

    private double density = DEFAULT_DENSITY;
    //private int numIterations = DEFAULT_NUM_ITERATIONS;


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
        CaveMap cave = new CaveMap(DEFAULT_CAVE_WIDTH, DEFAULT_CAVE_HEIGHT, density);
        renderer = new CaveRenderer(DEFAULT_WIDTH, DEFAULT_HEIGHT, cave);
    }

    /*
    public void setNumIterations(int num) {
        if (num != this.numIterations) {
            numIterations = num;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }  */



    public void setDensity(double density) {
        if (this.density != density)  {
            this.density = density;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    private void requestRestart(int width, int height) {
        try {
            CaveMap cave = new CaveMap(DEFAULT_CAVE_WIDTH, DEFAULT_CAVE_HEIGHT, density);
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


        return false;
    }
}
