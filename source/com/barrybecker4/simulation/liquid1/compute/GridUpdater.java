/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.compute;

import com.barrybecker4.simulation.liquid1.Logger;
import com.barrybecker4.simulation.liquid1.model.Grid;
import com.barrybecker4.simulation.liquid1.model.Particles;

import javax.vecmath.Vector2d;

import static com.barrybecker4.simulation.common.PhysicsConstants.ATMOSPHERIC_PRESSURE;

/**
 *  Updates the grid physics.
 *  adapted from work by Nick Foster.
 *  See http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
 *
 *  Improvements:
 *    - increase performance by only keeping track of particles near the surface.
 *    - allow configuring walls from file.
 *
 *  @author Barry Becker
 */
public class GridUpdater {

    /**
     * Viscosity of the liquid. Larger for molasses (.3), smaller for kerosene (.0001)
     * Water is about .001 Ns/m^2 or .01 g/s mm
     */
    public static final double DEFAULT_VISCOSITY = 0.002;

    /** Used in mass conservation (how?) */
    public static final double DEFAULT_B0 = 1.4;  // 1.7

    /** the grid of cells that make up the environment */
    private Grid grid ;

    private  double viscosity = DEFAULT_VISCOSITY;

    /** used in mass conservation as a relaxation constant */
    private double b0 = DEFAULT_B0;

    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public GridUpdater(Grid grid) {

        this.grid = grid;
    }

    public void setViscosity(double v) {
        viscosity = v;
    }

    public void setB0(double b0) {
        this.b0 = b0;
    }

    /**
     * Compute tilde velocity of each cell
     */
    public void updateVelocity( double timeStep, double gravity ) {

        Logger.log( 1, "stepForward: about to update the velocity field (timeStep=" + timeStep + ')' );
        int i, j;
        Vector2d force = new Vector2d(0, gravity);
        VelocityUpdater velocityUpdater = new VelocityUpdater();

        for ( j = 1; j < grid.getYDimension() - 1; j++ ) {
            for ( i = 1; i < grid.getXDimension() - 1; i++ ) {
                velocityUpdater.updateTildeVelocities(grid.getCell(i, j), grid.getNeighbors(i,j),
                                               grid.getCell(i - 1, j + 1), grid.getCell(i + 1, j - 1),
                                               timeStep, force, viscosity );
            }
        }
        for ( j = 1; j < grid.getYDimension() - 1; j++ ) {
            for ( i = 1; i < grid.getXDimension() - 1; i++ ) {
                grid.getCell(i, j).swap();
            }
        }
    }

    /**
     * perform pressure iteration to consider mass conservation.
     * repeat till all cells in the flow field have a divergence less than EPSILON.
     * When things go bad, this can take 50-70 or more iterations.
     * RISK: 6
     * @return the maximum divergence of any of the cells in the grid.
     */
    public double updatePressure( double timeStep ) {

        PressureUpdater updater = new PressureUpdater(grid, b0);
        double div = updater.updatePressure(timeStep);

         if (updater.getNumIterations() > 10) {
            Logger.log(1, " updatePress: converged to maxDiv = " + div
                    + " after " + updater.getNumIterations() + " iterations.");
        }
        return div;
    }

    /**
     * compute velocity and pressure of SURFACE cells.
     */
    public void updateSurfaceVelocity() {

        SurfaceVelocityUpdater surfaceUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE);

        for (int j = 1; j < grid.getYDimension() - 1; j++ ) {
            for (int i = 1; i < grid.getXDimension() - 1; i++ ) {
                surfaceUpdater.updateSurfaceVelocities(grid.getCell(i, j), grid.getNeighbors(i, j));
            }
        }
    }

    /**
     * move particles according to vector field.
     * updates the timeStep if the cfl condition is not met.
     * RISK: 3
     * @return the current timeStep (it was possible adjusted)
     */
    public double updateParticlePosition( double timeStep, Particles particles) {

        ParticleAdvector updater = new ParticleAdvector(grid);
        return updater.advectParticles(timeStep, particles);
    }
}
