/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.compute;

import com.barrybecker4.simulation.liquid1.Logger;
import com.barrybecker4.simulation.liquid1.model.Cell;
import com.barrybecker4.simulation.liquid1.model.CellDimensions;
import com.barrybecker4.simulation.liquid1.model.CellStatus;
import com.barrybecker4.simulation.liquid1.model.Grid;
import com.barrybecker4.simulation.liquid1.model.Particle;
import com.barrybecker4.simulation.liquid1.model.Particles;

import javax.vecmath.Vector2d;

/**
 *  Updates positions of the particle markers in the grid.
 *
 *  @author Barry Becker
 */
public class ParticleAdvector {

    /** the grid of cells that make up the environment */
    private Grid grid ;

    private static final double EPSILON = 0.0000001;

    VelocityInterpolator interpolator;

    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public ParticleAdvector(Grid grid) {

        this.grid = grid;
        interpolator = new VelocityInterpolator(grid);
    }

    /**
     * move particles according to vector field.
     * updates the timeStep if the cfl condition is not met.
     * RISK: 3
     * @return the current timeStep (it was possible adjusted)
     */
    public double advectParticles(double timeStep, Particles particles) {

        // keep track of the biggest velocity magnitude so we can adjust the timestep if needed.
        double maxLength = Double.MIN_VALUE;

        for (Particle particle : particles)  {

            double length = advectParticle(timeStep, particle);

            if (length > maxLength)
                maxLength = length;
        }

        adjustTimeStep(timeStep, maxLength);
        return timeStep;
    }

    /**
     * The velocity of a particle is determined using area weighting interpolation.
     * @return advect a particle if it is completely under water.
     */
    private double advectParticle(double timeStep, Particle particle) {

        int i = (int) Math.floor(particle.x);
        int j = (int) Math.floor(particle.y);
        CellStatus status = grid.getCell(i, j).getStatus();
        double magnitude = 0;

        if ( status == CellStatus.FULL || status == CellStatus.SURFACE || status == CellStatus.ISOLATED ) {

            magnitude = advectWaterParticle(timeStep, particle, i, j);
        }
        return magnitude;
    }

    /**
     * Advect a single water particle.
     * @return  the magnitude of its velocity vector.
     */
    private double advectWaterParticle(double timeStep, Particle particle, int i, int j) {

        Vector2d vel = interpolator.findVelocity(particle);

        // scale the velocity by the cell size so we can assume the cells have unit dims
        vel.scale(CellDimensions.INVERSE_CELL_SIZE);

        double xChange = timeStep * vel.x;
        double yChange = timeStep * vel.y;
        particle.set( particle.x + xChange, particle.y + yChange );
        particle.incAge( timeStep );

        Cell newHomeCell = findNewHomeCell(particle, i, j);

        assert ( particle.x >= 1 && particle.y >= 1
                && particle.x < grid.getXDimension() - 1
                && particle.y < grid.getYDimension() - 1) :
                "particle.x=" + particle.x + "particle.y=" + particle.y ;

        // adjust # particles as they cross cell boundaries
        newHomeCell.incParticles(); // increment new cell
        grid.getCell(i, j).decParticles();  // decrement last cell
        particle.setCell( newHomeCell );

        assert ( grid.getCell(i, j).getNumParticles() >= 0):
                "The number of particles in grid[" + i + "][" + j + "] is " + grid.getCell(i, j).getNumParticles();
        assert ( newHomeCell.getNumParticles() >= 0 );
        return vel.length();
    }

    /**
     * Determine the cell that the particle is in now after moving it. May be same as it was.
     * Ensure the liquid does not enter an OBSTACLE.
     * @return new Cell that the particle is in. Not necessarily different than original.
     */
    private Cell findNewHomeCell(Particle particle, int i, int j) {

        int ii = (int) Math.floor(particle.x);
        int jj = (int) Math.floor(particle.y);

        if (ii < 0) {
            particle.x = 0.0;
            ii = 0;
        }
        if (jj < 0) {
            particle.y = 0.0;
            jj = 0;
        }
        if (ii >= grid.getXDimension()) {
            ii = grid.getXDimension() - 1;
            particle.x = ii;

        }
        if (jj >= grid.getYDimension()) {
            jj = grid.getYDimension() - 1;
            particle.y = jj;
        }


        // move outside the obstacle if we find ourselves in one
        if ( grid.getCell(ii, jj).getStatus() == CellStatus.OBSTACLE ) {
            if ( i < ii ) {
                particle.set( ii - EPSILON, particle.y );
            }
            else if (i > ii ) {
                particle.set( ii + 1.0 + EPSILON, particle.y );
            }
            if ( j < jj ) {
                particle.set( particle.x, jj - EPSILON );
            }
            else if ( j > jj ) {
                particle.set( particle.x, jj + 1.0 + EPSILON );
            }
        }

        ii = (int) particle.x;
        jj = (int) particle.y;
        return grid.getCell(ii, jj);
    }

    /**
     * Adaptively change the time step if needed.
     * @return new timeStep to use. May be unchanged.
     */
    private double adjustTimeStep(double timeStep, double maxLength) {

        // max distance to go in one step. Beyond this, we apply the governor.
        double maxDistance = CellDimensions.CELL_SIZE / 10.0;
        double minDistance = CellDimensions.CELL_SIZE / 10000.0;

        // adjust the timestep if needed.
        double increment = (timeStep * maxLength);
        double newTimeStep = timeStep;
        if (increment > maxDistance) {
            newTimeStep /= 2.0;
            Logger.log(1, "advectParticles: HALVED dt=" + timeStep +" increment="+increment );
        }
        else if (increment < minDistance) {
            newTimeStep *= 2.0;
            Logger.log(1, "advectParticles: DOUBLED dt=" + timeStep
                    +" increment="+increment +" maxLength=" + maxLength);
        }
        return newTimeStep;
    }
}
