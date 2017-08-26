/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.model;

import javax.vecmath.Vector2d;
import java.text.DecimalFormat;

/**
 *  This is the global space containing all the cells, walls, and particles
 *  Assumes an M*N grid of cells.
 *  X axis increases to the left
 *  Y axis increases downwards to be consistent with java graphics.
 *  adapted from work by Nick Foster.
 *  See
 *  http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
 *
 *  @author Barry Becker
 */
public class Grid {

    // the dimensions of the space
    private int xDim;
    private int yDim;

    /** the grid of cells that make up the environment in x,y (col, row) order */
    private Cell[][] grid = null;

    private DecimalFormat formatter = new DecimalFormat("###0.###");

    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public Grid(int xDim, int yDim) {

        this.xDim = xDim;
        this.yDim = yDim;
        grid = new Cell[xDim][yDim];

        for (int j = 0; j < this.yDim; j++ ) {
            for (int i = 0; i < xDim; i++ ) {
                grid[i][j] = new Cell();
            }
        }
    }

    public Cell getCell(int i, int j) {
        return grid[i][j];
    }

    public CellNeighbors getNeighbors(int i, int j) {
        return new CellNeighbors(grid[i + 1][j],   grid[i - 1][j],
                                 grid[i][j + 1],   grid[i][j - 1] );
    }

    public int getXDimension() {
        return xDim;
    }

    public int getYDimension() {
        return yDim;
    }

    public void setVelocity(int i, int j, Vector2d velocity) {
        grid[i][j].initializeU(velocity.x);
        grid[i][j].initializeV(velocity.y);
    }

    /**
     * Update the cell status for all the cells in the grid.
     */
    public void updateCellStatus() {
        int i, j;
        for (j = 1; j < yDim - 1; j++ ) {
            for (i = 1; i < xDim - 1; i++ ) {
                grid[i][j].updateStatus(getNeighbors(i,j));
            }
        }
    }

    /**
     * setup the obstacles.
     */
    public void setBoundaries() {

        // right and left
        for (int j = 0; j < yDim; j++ ) {
            grid[0][j].setStatus( CellStatus.OBSTACLE );
            grid[xDim - 1][j].setStatus( CellStatus.OBSTACLE );
        }
        // top and bottom
        for (int i = 0; i < xDim; i++ ) {
            grid[i][0].setStatus( CellStatus.OBSTACLE );
            grid[i][yDim - 1].setStatus( CellStatus.OBSTACLE );
        }
    }

    /**
     * Set OBSTACLE condition of stationary objects, inflow/outflow.
     */
    public void setBoundaryConstraints() {

        // right and left
        for (int j = 0; j < yDim; j++ ) {
            // left
            Cell n = grid[1][j];
            grid[0][j].setPressure( n.getPressure() );
            grid[0][j].initializeVelocity(0, n.getV());   // -n.getV ???
            // right
            n = grid[xDim - 2][j];
            grid[xDim - 1][j].setPressure( n.getPressure() );
            grid[xDim - 1][j].initializeVelocity(0, n.getV());  // -n.getV()
            grid[xDim - 2][j].initializeU(0);
        }

        // top and bottom
        for (int i = 0; i < xDim; i++ ) {
            // bottom
            Cell n = grid[i][1];
            grid[i][0].setPressure( n.getPressure() );
            grid[i][0].initializeVelocity(n.getU(), 0); // -n.getU() ???
            // top
            n = grid[i][yDim - 2];
            grid[i][yDim - 1].setPressure( n.getPressure() );
            grid[i][yDim - 1].initializeVelocity(n.getU(), 0);  // -n.getU()
            grid[i][yDim - 2].initializeV(0);
        }
    }

    public String toString() {

        StringBuilder bldr = new StringBuilder();

        for (int j = yDim -1; j >=0 ; j-- ) {
            for (int i = 0; i < xDim; i++ ) {
                Cell cell = getCell(i, j);
                bldr.append("    V=" +format(cell.getV()));
                bldr.append("    |");
            }
            bldr.append("\n");
            for (int i = 0; i < xDim; i++ ) {
                Cell cell = getCell(i, j);
                bldr.append("P=" + format(cell.getPressure()));
                bldr.append(" U=" + format(cell.getU()));
                bldr.append("|");
            }
            bldr.append("\n");
            for (int i = 0; i < xDim; i++ ) {
                bldr.append("----------------");
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }

    private String format(double num) {
        StringBuilder fmtNum = new StringBuilder(formatter.format(num));
        while (fmtNum.length() < 5) {
            fmtNum.append(' ');
        }
        return fmtNum.toString();
    }
}
