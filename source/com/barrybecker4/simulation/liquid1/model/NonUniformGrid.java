/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.model;

import javax.vecmath.Vector2d;

/**
 *  Create a grid with non-uniform velocity throughout.
 *  The velocity will be set in the middle and taper off toward 0 at the edges.
 *
 *  @author Barry Becker
 */
public class NonUniformGrid extends Grid {

    /**
     * Constructor.
     */
    public NonUniformGrid(int xDim, int yDim, Vector2d velocity) {

        this(xDim, yDim, velocity, CellStatus.EMPTY);
    }

    /**
     * Constructor.
     */
    public NonUniformGrid(int xDim, int yDim, Vector2d velocity, CellStatus status) {

        super(xDim, yDim);
        double centerX = xDim/2 + 0.5;
        double centerY = yDim/2 + 0.5;
        double maxDist = Math.sqrt(centerX * centerX + centerY * centerY);

        for (int j = 1; j < yDim-1; j++ ) {
            for (int i = 1; i < xDim-1; i++ ) {
                double xDiff = centerX-i;
                double yDiff = centerY-j;
                double dist = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
                double scale = (maxDist - dist)/maxDist;
                Vector2d vel = new Vector2d(velocity.x*scale, velocity.y*scale);
                setVelocity(i, j, vel);
                this.getCell(i, j).setStatus(status);
            }
        }
    }

}
