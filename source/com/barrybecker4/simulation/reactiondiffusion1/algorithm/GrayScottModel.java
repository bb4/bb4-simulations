/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion1.algorithm;

import java.awt.*;


/**
 * Data structure for the Gray-Scott algorithm.
 * @author Barry Becker.
 */
public final class GrayScottModel {

    public static final double K0 = 0.079;
    public static final double F0 = 0.02;

    private static final double INITIAL_U = 0.5;
    private static final double INITIAL_V = 0.25;

    /** concentrations of the 2 chemicals, u and v. */
    double[][] u;
    double[][] v;
    double[][] tmpU;
    double[][] tmpV;

    private double k;
    private double f;
    private double initialK;
    private double initialF;

    private int width, height;


    /**
     * Constructor
     * @param width width of computational space.
     * @param height height of computational space.
     */
    GrayScottModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.initialF = F0;
        this.initialK = K0;
        resetState();
    }

    public double getU(int x, int y) {
        return u[x][y];
    }
    public double getV(int x, int y) {
        return v[x][y];
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void setSize(Dimension requestedNewSize) {
        width = requestedNewSize.width;
        height = requestedNewSize.height;
    }

    public void setF(double f) {
        this.f = f;
    }
    double getF() {
        return f;
    }

    public void setK(double k) {
        this.k = k;
    }
    double getK() {
        return k;
    }

    /**
     * Exchange the u, v fields with the tmp versions.
     */
    void commitChanges() {
        double[][] temp = tmpU;
        tmpU = u;
        u = temp;

        temp = tmpV;
        tmpV = v;
        v = temp;
    }

    double getNeighborSum(double tmp[][], int x, int y) {

       return tmp[x + 1][y]
                + tmp[x - 1][y]
                + tmp[x][y + 1]
                + tmp[x][y - 1];
    }

    double getEdgeNeighborSum(double tmp[][], int x, int y) {

        return tmp[getPeriodicXValue(x + 1, width)][y]
                  + tmp[getPeriodicXValue(x - 1, width)][y]
                  + tmp[x][getPeriodicXValue(y + 1, height)]
                  + tmp[x][getPeriodicXValue(y - 1, height)];
    }

    /**
     * Create some initial pattern of chemical that represents the initial condition.
     */
    void resetState()  {

        f = initialF;
        k = initialK;

        u = new double[width][height];
        v = new double[width][height];
        tmpU = new double[width][height];
        tmpV = new double[width][height];

        stampInitialSquare(0, 0, width, height, 1, 0);

        // random square 1
        int w3 = width / 3;
        int h3 = height / 3;
        stampInitialSquare(w3, h3, w3, h3, INITIAL_U, INITIAL_V);

        // random square 2
        int w7 = width / 7;
        int h5 = height / 5;
        stampInitialSquare(5*w7, 3*h5, w7, h5, INITIAL_U, INITIAL_V);
    }

    /**
     * Place a square of chemicals with the initial concentrations.
     */
    private void stampInitialSquare(int startX, int startY, int width, int height, double initialU, double initialV) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tmpU[startX + x][startY + y] = initialU;
                tmpV[startX + x][startY + y] = initialV;
            }
        }
    }

    /**
     * Periodic boundary conditions.
     * @return new x value taking into account wrapping boundaries.
     */
    private static int getPeriodicXValue(int x, int max) {
        int xp = x % max;
        return xp<0 ? xp + max : xp;
    }
}
