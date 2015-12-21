/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import com.barrybecker4.simulation.liquid.model.*;
import junit.framework.TestCase;
import org.junit.Test;

import javax.vecmath.Vector2d;

import static com.barrybecker4.simulation.common.PhysicsConstants.ATMOSPHERIC_PRESSURE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * There are 13 surface conditions.
 * 1-4) One of the 4 immediate neighbors is empty
 * 5-8) Two of the 4 immediate neighbors is empty
 * 9-12) Three of the 4 immediate neighbors is empty
 * 13) isolated
 *
 * @author Barry Becker
 */
public class SurfaceVelocityUpdaterTest {

    private static final double EPS = 0.00001;
    private static final int DIM = 6;
    private static final double TOL = 0.0000000001;

    /** instance under test. */
    private SurfaceVelocityUpdater svUpdater;


    @Test
    public void testUpdateIsolatedInAbsenceOfFlow() {

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.EMPTY);
        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.ISOLATED);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        svUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE);
        svUpdater.updateSurfaceVelocities(cell, neighbors);

        assertEquals("Unexpected pressure", ATMOSPHERIC_PRESSURE, cell.getPressure(), TOL);
        verifyBorderVelocities(cell, neighbors, 0, 0, 0, 0);
    }

    @Test
    public void testUpdateIsolatedInNorthEastFlow() {

        Grid grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.EMPTY);
        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.ISOLATED);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);

        svUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE);
        svUpdater.updateSurfaceVelocities(cell, neighbors);

        assertEquals("Unexpected pressure", ATMOSPHERIC_PRESSURE, cell.getPressure(), TOL);
        verifyBorderVelocities(cell, neighbors, 0.285714285714, 0.28571428, 0.2054924, 0.205492455313);
    }

    /*
    @Test
    public void testUpdateSurfaceWithOneNorthEmptyNbrInNorthEastFlow() {

        Grid grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.SURFACE);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        grid.getCell(2, 3).setStatus(CellStatus.EMPTY);

        svUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE);
        svUpdater.updateSurfaceVelocities(cell, neighbors);

        assertEquals("Unexpected pressure", ATMOSPHERIC_PRESSURE, cell.getPressure(), TOL);
        verifyBorderVelocities(cell, neighbors, 0.2857142857, 0.285714285714, 0.205492455313, 0.2054924553);
    }

    @Test
    public void testUpdateSurfaceWithOneEastEmptyNbrInNorthEastFlow() {

        Grid grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.SURFACE);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        grid.getCell(3, 2).setStatus(CellStatus.EMPTY);

        svUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE);
        svUpdater.updateSurfaceVelocities(cell, neighbors);

        assertEquals("Unexpected pressure", ATMOSPHERIC_PRESSURE, cell.getPressure(), TOL);
        verifyBorderVelocities(cell, neighbors, 0.2857142857, 0.2857142857, 0.205492455, 0.205492455);
    }

    @Test
    public void testUpdateSurfaceWithOneWestEmptyNbrInNorthEastFlow() {

        Grid grid = new NonUniformGrid(DIM, DIM, new Vector2d(0.5, 0.5), CellStatus.FULL);
        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.SURFACE);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        grid.getCell(1, 2).setStatus(CellStatus.EMPTY);

        svUpdater = new SurfaceVelocityUpdater(ATMOSPHERIC_PRESSURE);
        svUpdater.updateSurfaceVelocities(cell, neighbors);

        assertEquals("Unexpected pressure", ATMOSPHERIC_PRESSURE, cell.getPressure(), TOL);
        verifyBorderVelocities(cell, neighbors, 0.2857142857, 0.2857142857, 0.2054924553, 0.2054924553);
    }  */

    private void verifyBorderVelocities(Cell cell, CellNeighbors neighbors,
                                   double U, double V, double leftU, double bottomV) {
        boolean valid = (Math.abs(U-cell.getU()) < EPS
                        && Math.abs(V - cell.getV()) < EPS
                        && Math.abs(leftU - neighbors.getLeft().getU()) < EPS
                        && Math.abs(bottomV - neighbors.getBottom().getV()) < EPS);

        if (!valid) {
            System.out.println("U="+ cell.getU() +" (expected "+ U + ")");
            System.out.println("V="+ cell.getV() +" (expected "+ V + ")");
            System.out.println("leftU="+ neighbors.getLeft().getU() +" (expected "+ leftU + ")");
            System.out.println("bottomV="+ neighbors.getBottom().getV() +" (expected "+ bottomV + ")");
        }
        assertTrue("Something out of range U=" + cell.getU() +" V=" + cell.getV()
                + " leftU="+ neighbors.getLeft().getU() +" bottomV="+ neighbors.getBottom().getV(),
                valid);
    }

}

