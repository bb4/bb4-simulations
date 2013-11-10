/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm;

import com.barrybecker4.simulation.common.RectangularModel;

/**
 * Nothing but a big matrix to hold the resulting values.
 *
 * @author Barry Becker
 */
public class FractalModel implements RectangularModel {

    double[][] values;
    private static final int FIXED_SIZE = 200;
    private int lastRow;
    private int currentRow;


    public FractalModel() {
        initialize(FIXED_SIZE, FIXED_SIZE);
    }

    /**
     * We can change the size of the model, but doing so will clear all current results.
     * We only resize if the new dimensions are different than we had to prevent clearing results unnecessarily.
     */
    public void setSize(int width, int height) {
        if (width != getWidth() || height != getHeight()) {
            initialize(width, height);
        }
    }

    private void initialize(int width, int height) {
        values = new double[width][height];
        currentRow = 0;
        lastRow = 0;
    }

    public void setValue(int x, int y, double value) {
        if (x < getWidth() && y < getHeight())
            values[x][y] = value;
    }

    public double getValue(int x, int y) {
        if (x<0 || x >= getWidth() || y<0 || y >= getHeight())  {
            return 0;
        }
        return values[x][y];
    }

    public int getWidth() {
        return values.length;
    }

    public int getHeight() {
        return values[0].length;
    }

    public double getAspectRatio() {
        return getWidth() / getHeight();
    }

    public boolean isDone() {
        return currentRow >= getHeight();
    }

    /**
     * Set the row that we have calculated up to.
     * @param row new row
     */
    public void setCurrentRow(int row) {
        lastRow = currentRow;
        currentRow = row;
        if (currentRow == 0) {
            lastRow = 0;
        }
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getLastRow() {
        return lastRow;
    }
}
