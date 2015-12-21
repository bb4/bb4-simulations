/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import junit.framework.TestCase;
import org.junit.Test;

import javax.vecmath.Vector2d;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Barry Becker
 */
public class VelocityUpdaterTest {

    /** delta time */
    private static final double DT = 0.1;
    private static final double GRAVITY = 10.0;
    private static final double VISCOSITY = 0.0;

    private static final Vector2d FORCE = new Vector2d(0.0, GRAVITY);
    private static final int DIM = 6;

    /** instance under test. */
    private VelocityUpdater velocityUpdater;


    @Test
    public void testTrue() {
        assertTrue(true);
    }
    /*
    public void testUpdateInAbsenceOfFlow() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        velocityUpdater = new VelocityUpdater();
        velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1),
                                              DT, FORCE, VISCOSITY);
        cell.swap();
        assertEquals("Unexpected UV", new Vector2d(0.0, 1.0), new Vector2d(cell.getU(), cell.getV()));
    }

    @Test
    public void testUpdateInSlightNorthEastFlow() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        velocityUpdater = new VelocityUpdater();
        velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1),
                                              DT, FORCE, VISCOSITY);
        cell.swap();
        assertEquals("Unexpected UV", new Vector2d(0.1, 1.1), new Vector2d(cell.getU(), cell.getV()));
    }

    @Test
    public void testUpdateInSlightNorthEastNonUniformFlow() {

        Grid grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        velocityUpdater = new VelocityUpdater();
        velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1),
                                              DT, FORCE, VISCOSITY);
        cell.swap();
        assertEquals("Unexpected UV",
                new Vector2d(0.057110615322103674, 1.0571106153221037),
                new Vector2d(cell.getU(), cell.getV()));
    }

    @Test
    public void testUpdateInViscousSlightNothEastFlowFlow() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.FULL);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        velocityUpdater = new VelocityUpdater();

        double viscosity = 10.0;
        velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1),
                                              DT, FORCE, viscosity);
        cell.swap();
        assertEquals("Unexpected UV", new Vector2d(0.1, 1.1), new Vector2d(cell.getU(), cell.getV()));
    }

    @Test
    public void testUpdateInSlightNothEastFlowFlowUpperLeftTweak() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.FULL);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        velocityUpdater = new VelocityUpdater();
        grid.getCell(1, 3).setU(-0.4);

        velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1),
                                              DT, FORCE, VISCOSITY);
        cell.swap();
        assertEquals("Unexpected UV", new Vector2d(0.1, 1.0997500000000002), new Vector2d(cell.getU(), cell.getV()));
    }

    @Test
    public void testUpdateInSlightNorthEastFlowFlowLoweRightTweak() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        velocityUpdater = new VelocityUpdater();
        grid.getCell(3, 1).setV(-0.4);

        velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1),
                                              DT, FORCE, VISCOSITY);
        cell.swap();
        assertEquals("Unexpected UV", new Vector2d(0.09975, 1.1), new Vector2d(cell.getU(), cell.getV()));
    }

    @Test
    public void testUpdateInSlightNorthEastFlowFlowLeftPressureTweak() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        velocityUpdater = new VelocityUpdater();

        grid.getCell(1, 2).setPressure(ATMOSPHERIC_PRESSURE/2);

        velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1),
                                              DT, FORCE, VISCOSITY);

        cell.swap();
        assertEquals("Unexpected UV", new Vector2d(0.10450000000000001, 1.1), new Vector2d(cell.getU(), cell.getV()));
    }

    @Test
    public void testUpdateInSlightNorthEastFlowFlowLowerPressureTweak() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.1, 0.1), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        velocityUpdater = new VelocityUpdater();

        grid.getCell(2, 1).setPressure(ATMOSPHERIC_PRESSURE/2);

        velocityUpdater.updateTildeVelocities(cell, neighbors, grid.getCell(1, 3), grid.getCell(3, 1),
                                              DT, FORCE, VISCOSITY);

        cell.swap();
        assertEquals("Unexpected UV", new Vector2d(0.1, 1.1045), new Vector2d(cell.getU(), cell.getV()));
    }  */
}

