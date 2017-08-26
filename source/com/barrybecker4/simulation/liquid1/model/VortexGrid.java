/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.model;

import javax.vecmath.Vector2d;

/**
 *  Create a grid with non-uniform velocity throughout.
 *  The velocity will be set in the middle and taper off toward 0 at the edges.
 *
 *  @author Barry Becker
 */
public class VortexGrid extends Grid {

    /**
     * Constructor.
     */
    public VortexGrid(int xDim, int yDim) {

        this(xDim, yDim, CellStatus.EMPTY);
    }

    /**
     * Constructor.
     */
    public VortexGrid(int xDim, int yDim, CellStatus status) {

        super(xDim, yDim);
        double centerX = xDim/2;
        double centerY = yDim/2;

        for (int j = 1; j < yDim-1; j++ ) {
            for (int i = 1; i < xDim-1; i++ ) {
                double xDiff = centerX-i;
                double yDiff = centerY-j;

                double theta = Math.atan2(yDiff, xDiff);
                Vector2d tangentialVec = new Vector2d(-Math.sin(theta), Math.cos(theta));

                setVelocity(i, j, tangentialVec);
                getCell(i, j).setStatus(status);
            }
        }
    }

}
