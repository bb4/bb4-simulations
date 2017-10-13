// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem1.model;

import com.barrybecker4.simulation.common1.Profiler;
import com.barrybecker4.simulation.lsystem1.rendering.LSystemRenderer;

import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;

/**
 * See https://en.wikipedia.org/wiki/L-system
 * For explanation of the grammar and different types of L-systems.
 * The language should be expanded to include support for more terms.
 * @author Barry Becker
 */
public class LSystemModel {

    public static final String DEFAULT_EXPRESSION = "F(+F)F(-F)F";
    public static final int DEFAULT_ITERATIONS = 1;
    public static final double DEFAULT_ANGLE = 90.0;
    public static final double DEFAULT_SCALE = 0.9;
    public static final double DEFAULT_SCALE_FACTOR = 0.7;
    private static final int DEFAULT_SIZE = 256;

    private LSystemRenderer renderer;

    private int numIterations;
    private double angle;
    private double scale;
    private double scaleFactor;
    private String expression;

    private boolean restartRequested = false;


    public LSystemModel() {
        reset();
    }

    public void setSize(int width, int height)  {

        if (width != renderer.getWidth() || height != renderer.getHeight())   {
            requestRestart(width, height);
        }
    }

    public void reset() {

        numIterations = DEFAULT_ITERATIONS;
        angle = DEFAULT_ANGLE;
        scale = DEFAULT_SCALE;
        scaleFactor = DEFAULT_SCALE_FACTOR;
        expression = DEFAULT_EXPRESSION;

        renderer = new LSystemRenderer(DEFAULT_SIZE, DEFAULT_SIZE, expression, numIterations, angle, scale, scaleFactor);
    }

    public void setNumIterations(int num) {
        if (num != this.numIterations) {
            numIterations = num;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    public void setAngle(double ang) {
        if (ang != angle)  {
            angle = ang;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    public void setScale(double value) {
        if (value != scale)  {
            scale = value;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    public void setScaleFactor(double value) {
        if (value != scaleFactor)  {
            scaleFactor = value;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    public void setExpression(String exp) {
        if (!exp.equals(expression))  {
            expression = exp;
            requestRestart(renderer.getWidth(), renderer.getHeight());
        }
    }

    private void requestRestart(int width, int height) {
        try {
            renderer = new LSystemRenderer(width, height, expression, numIterations, angle, scale, scaleFactor);
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
