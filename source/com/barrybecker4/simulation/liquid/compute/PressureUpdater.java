/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import com.barrybecker4.simulation.liquid.model.Grid;

/**
 *  Iterates, conserving mass, until the pressure stabilizes.
 *
 *  @author Barry Becker
 */
public class PressureUpdater {

    /** the grid of cells that make up the environment */
    private Grid grid ;

    private double b0;
    private static final double EPSILON = 0.000000001;
    private int iterationCount;


    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public PressureUpdater(Grid grid,  double b0) {

        this.grid = grid;
        this.b0 = b0;
    }

    /**
     * @return  the number of iterations it took to converge.
     */
    public int getNumIterations() {
       return iterationCount;
    }

    /**
     * perform pressure iteration to consider mass conservation.
     * repeat till all cells in the flow field have a divergence less than EPSILON.
     * When things go bad, this can take 50-70 or more iterations.
     * RISK: 6
     * @return the maximum divergence of any of the cells in the grid.
     */
    public double updatePressure( double timeStep ) {

        double maxDivergence;
        double divergence;

        MassConserver conserver = new MassConserver(b0, timeStep);

        do {
            // adjust tilde velocities to satisfy mass conservation
            maxDivergence = 0;
            for (int j = 1; j < grid.getYDimension()-1; j++ ) {
                for (int i = 1; i < grid.getXDimension()-1; i++ ) {
                    divergence = conserver.updateMassConservation(grid.getCell(i, j), grid.getNeighbors(i, j));
                    if ( divergence > maxDivergence ) {
                        maxDivergence = divergence;
                    }
                }
            }
            iterationCount++;

        } while ( maxDivergence > EPSILON );

        return maxDivergence;
    }
}
