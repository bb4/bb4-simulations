/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.henonphase.algorithm;

import java.awt.*;

/**
 * Henon traveler travels through time
 * @author Barry Becker
 */
public class Traveler {

    private Color color;
    private TravelerParams params;

    // current position
    private double x;
    private double y;

    // last position
    private double lastX;
    private double lastY;


    Traveler(double origX, double origY, Color color, TravelerParams params) {
        this.color = color;
        this.params = params;
        x = origX;
        y = origY;
        lastX = x;
        lastY = y;
    }

    public double getX() {return x;}
    public double getY() {return y;}

    double getLastX() {return lastX;}
    double getLastY() {return lastY;}

    public Color getColor() {
        return color;
    }

    /** increment forward one iteration */
    public void increment() {

        lastX = x;
        lastY = y;

        double sin = Math.sin(params.getAngle());
        double cos = Math.cos(params.getAngle());
        double term = params.getMultiplier() * y + params.getOffset() - x * x;

        double temp = x * cos - term * sin;
        y = x * sin + term * cos;
        x = temp;
    }
}
