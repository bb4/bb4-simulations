/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid.model;

import com.barrybecker4.simulation.common.RectangularModel;

/**
 * Data behind the Fluid.
 * @author Barry Becker
 */
public class Grid implements RectangularModel {

    private int dimX;
    private int dimY;

    private CellGrid grid0;
    private CellGrid grid1;

    /**
     * Creates a new instance of Grid
     */
    public Grid(int dimX, int dimY) {

        this.dimX = dimX;
        this.dimY = dimY;

        grid0 = new CellGrid(dimX, dimY);
        grid1 = new CellGrid(dimX, dimY);

        grid1.addInitialInkDensity();
    }

    public int getWidth() {
        return dimX;
    }

    public int getHeight() {
        return dimY;
    }

    CellGrid getGrid0() {
        return grid0;
    }

    CellGrid getGrid1() {
        return grid1;
    }

    public double getU(int i, int j) {
        return grid1.u[i][j];
    }

    public double getV(int i, int j) {
        return grid1.v[i][j];
    }

    /** used for rendering. */
    public double getValue(int i, int j) {
        return getDensity(i, j);
    }

    public int getCurrentRow() {
        return getHeight();
    }

    public int getLastRow() {
        return 0;
    }

    public double getDensity(int i, int j) {
        return grid1.density[i][j];
    }

    /**
     * Swap x[0] and x[1] arrays
     */
    public void swap(CellProperty prop) {

        double[][] temp = grid0.getProperty(prop);
        grid0.setProperty(prop, grid1.getProperty(prop));
        grid1.setProperty(prop, temp);
    }

     public void incrementU(int i, int j, double value) {
        grid0.u[i][j] += value;
     }

     public void incrementV(int i, int j, double value) {
        grid0.v[i][j] += value;
    }

     public void incrementDensity(int i, int j, double value) {
        grid0.density[i][j] += value;
    }

    /**
     * Set a boundary to contain the liquid.
     * @param b
     * @param x
     */
    public void setBoundary(Boundary b, double[][] x)  {

        for (int i = 1; i<= dimX; i++ ) {
            x[i][0] = b==Boundary.HORIZONTAL ? -x[i][1] : x[i][1];
            x[i][dimY +1] = b==Boundary.HORIZONTAL ? -x[i][dimY] : x[i][dimY];
        }
        for (int i = 1; i<= dimY; i++ ) {
            x[0][i] = b==Boundary.VERTICAL ? -x[1][i] : x[1][i];
            x[dimX +1][i] = b==Boundary.VERTICAL ? -x[dimX][i] : x[dimX][i];
        }

        x[0][ 0] = 0.5f * (x[1][0] + x[0][1]);
        x[0][dimY +1] = 0.5f * (x[1][dimY +1] + x[0][dimY]);
        x[dimX +1][0] = 0.5f * (x[dimX][0] + x[dimX +1][1]);
        x[dimX +1][dimY +1] = 0.5f*(x[dimX][dimY +1] + x[dimX +1][dimY]);
    }
}
