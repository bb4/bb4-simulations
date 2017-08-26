/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.model;

import com.barrybecker4.simulation.liquid1.Logger;
import com.barrybecker4.simulation.liquid1.compute.GridUpdater;
import com.barrybecker4.simulation.liquid1.config.Conditions;
import com.barrybecker4.simulation.liquid1.config.Region;
import com.barrybecker4.simulation.liquid1.config.Source;

import javax.vecmath.Vector2d;

/**
 *  This is the global space containing all the cells, walls, and particles
 *  Assumes an M*N grid of cells.
 *  X axis increases to the left
 *  Y axis increases downwards to be consistent with java graphics.
 *  Adapted from work by Nick Foster.
 *  See http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
 *
 *  Improvements:
 *    - increase performance by only keeping track of particles near the surface.
 *    - allow configuring walls from file.
 *
 *  @author Barry Becker
 */
public class LiquidEnvironment {

    private static final int NUM_RAND_PARTS = 1;

    /** the grid of cells that make up the environment */
    private Grid grid;

    /** Does all the computational processing on the grid */
    GridUpdater gridUpdater;

    /** constraints and conditions from the configuration file. */
    private Conditions conditions;

    // the set of particles in this simulation
    private Particles particles;

    /** the time since the start of the simulation  */
    private double time = 0.0;

    private boolean advectionOnly = false;

    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public LiquidEnvironment( String configFile ) {

        initializeFromConfigFile(configFile);
    }

    private void initializeFromConfigFile(String configFile) {

        conditions = new Conditions(configFile);
        initEnvironment();
    }

    public void reset() {

        initEnvironment();
    }

    private void initEnvironment() {

        int xDim = conditions.getGridWidth() + 2;
        int yDim = conditions.getGridHeight() + 2;

        grid = new VortexGrid(xDim, yDim);
        particles = new Particles();
        gridUpdater = new GridUpdater(grid);

        setInitialLiquid();
        grid.setBoundaries();
        setConstraints();
    }

    public int getWidth() {
        return ((grid.getXDimension() + 2 ) );
    }

    public int getHeight() {
        return ((grid.getYDimension() + 2) );
    }

    public Grid getGrid() {
        return grid;
    }

    public Particles getParticles() {
        return particles;
    }

    public void setViscosity(double v) {
        gridUpdater.setViscosity(v);
    }

    public void setB0(double b0) {
        gridUpdater.setB0(b0);
    }


    public boolean getAdvectionOnly() {
        return advectionOnly;
    }
    public void setAdvectionOnly(boolean advectOnly) {
        advectionOnly = advectOnly;
    }
    /**
     * Steps the simulation forward in time.
     * If the timestep is too big, inaccuracy and instability will result.
     * To prevent the instability we halve the timestep until the
     * Courant-Friedrichs-Levy condition is met.
     * In other words a particle should not be able to move more than a single cell
     * magnitude in a given timestep.
     * @return new new timeStep to use.
     */
    public double stepForward( double timeStep ) {

        // Update cell status so we can track the surface.
        grid.updateCellStatus();

        // Set up obstacle conditions for the free surface and obstacle cells
        setConstraints();

        if (!advectionOnly) {
            // Compute velocities for all full cells.
            gridUpdater.updateVelocity(timeStep, conditions.getGravity());

            ////System.out.println(grid.toString());

            // Compute the pressure for all Full Cells.
            gridUpdater.updatePressure(timeStep);

            // Re-calculate velocities for Surface cells.
            gridUpdater.updateSurfaceVelocity();
        }

        // Update the position of the surface and objects.
        double newTimeStep = gridUpdater.updateParticlePosition(timeStep, particles);

        time += newTimeStep;
        Logger.log(1, " the Time= " + time);
        return newTimeStep;
    }

    private void setInitialLiquid() {
        for (Region region : conditions.getInitialLiquidRegions()) {
            for (int i = region.getStart().getX(); i <= region.getStop().getX(); i++ ) {
                 for (int j = region.getStart().getY(); j <= region.getStop().getY(); j++ ) {
                     particles.addRandomParticles(i, j, 4 * NUM_RAND_PARTS, grid);
                 }
            }
        }
    }

    private void setConstraints() {
        grid.setBoundaryConstraints();

        //addWalls();
        addSources();
        //addSinks();
    }

    public void addSources() {
        for (Source source : conditions.getSources()) {
            addSource(source);
        }
    }

    private void addSource(Source source) {
        //add a spigot of liquid
        Vector2d velocity = source.getVelocity();

        if (source.isOn(time)) {
            for (int i = source.getStart().getX(); i <= source.getStop().getX(); i++ ) {
                 for (int j = source.getStart().getY(); j <= source.getStop().getY(); j++ ) {
                     grid.setVelocity(i, j, velocity);
                     particles.addRandomParticles(i, j, NUM_RAND_PARTS, grid);
                 }
            }
        }
    }

}
