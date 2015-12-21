/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import com.barrybecker4.simulation.liquid.model.*;
import org.junit.Test;

import javax.vecmath.Vector2d;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Barry Becker
 */
public class MassConserverTest {

    /** delta time */
    private static final double DT = 0.1;
    private static final double EPS = 0.0000000001;

    private static final int DIM = 6;
    private static final double TOL = 0.000000000001;


    /** instance under test. */
    private MassConserver conserver;

    @Test
    public void testMassConservationForEmptyCell() {

        double b0 = 1.0;
        conserver = new MassConserver(b0, DT);

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0));
        Cell cell = grid.getCell(2, 2);
        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        double div = conserver.updateMassConservation(cell, neighbors);

        assertEquals("Unexpected divergence", 0.0, div, TOL);
    }

    @Test
    public void testMassConservationForFullCellInUniformFlow() {

        double b0 = 1.0;
        conserver = new MassConserver(b0, DT);

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0));

        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.FULL);

        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        double div = conserver.updateMassConservation(cell, neighbors);

        assertEquals("Unexpected divergence", 0.0, div, EPS);
        assertEquals("Unexpected cell pressure", 0.9, cell.getPressure(), EPS);
        verifyResult(cell, neighbors, new Vector2d(1.0, 1.0),  new Vector2d(1.0, 1.0));
    }

    @Test
    public void testMassConservationForFullCellInNonUniformFlow() {

        double b0 = 1.0;
        conserver = new MassConserver(b0, DT);

        Grid grid = new NonUniformGrid(DIM, DIM, new Vector2d(1.0, 1.0));

        Cell cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.FULL);

        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        double div = conserver.updateMassConservation(cell, neighbors);

        assertEquals("Unexpected divergence", 0.032088732160504604, div, EPS);
        // @@ seems wrong
        assertEquals("Unexpected cell pressure", -7.122183040126151, cell.getPressure(), EPS);
        verifyResult(cell, neighbors, new Vector2d(0.49120674102731, 0.49120674102731),
                                      new Vector2d(0.49120674102731, 0.49120674102731));
    }

    @Test
    public void testMassConservationForNonUniformFlowHighB() {

        double b0 = 2.0;
        conserver = new MassConserver(b0, DT);

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0));
        Cell cell = grid.getCell(1, 2);
        cell.setU(0.5); // slower from west

        cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.FULL);

        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        double div = conserver.updateMassConservation(cell, neighbors);

        assertEquals("Unexpected divergence", 0.05, div, TOL);
        assertEquals("Unexpected cell pressure", -24.1, cell.getPressure(), TOL);
        verifyResult(cell, neighbors, new Vector2d(0.75, 0.75),  new Vector2d(0.75, 1.25));
    }

    @Test
    public void testMassConservationForNonUniformFlowSlowWest() {

        double b0 = 1.0;
        conserver = new MassConserver(b0, DT);

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0));
        Cell cell = grid.getCell(1, 2);
        cell.setU(0.5); // slower from west

        cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.FULL);

        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        double div = conserver.updateMassConservation(cell, neighbors);

        assertEquals("Unexpected divergence", 0.05, div, EPS);
        assertEquals("Unexpected cell pressure", -11.6, cell.getPressure(), EPS);
        verifyResult(cell, neighbors, new Vector2d(0.875, 0.875),  new Vector2d(0.625, 1.125));
    }

    @Test
    public void testMassConservationForNonUniformFlowSlowEast() {

        double b0 = 1.0;
        conserver = new MassConserver(b0, DT);

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0));
        Cell cell = grid.getCell(2, 2);
        cell.setU(0.5); // slower from east
        cell.setStatus(CellStatus.FULL);

        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        double div = conserver.updateMassConservation(cell, neighbors);

        assertEquals("Unexpected divergence", 0.05, div, EPS);
        assertEquals("Unexpected cell pressure", 13.4, cell.getPressure(), EPS);
        verifyResult(cell, neighbors, new Vector2d(0.625, 1.125),  new Vector2d(0.875, 0.875));
    }

    @Test
    public void testMassConservationForNonUniformFlowSlowSouth() {

        double b0 = 1.0;
        conserver = new MassConserver(b0, DT);

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0));
        Cell cell = grid.getCell(2, 1);
        cell.setV(0.5); // slower from south

        cell = grid.getCell(2, 2);
        cell.setStatus(CellStatus.FULL);

        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        double div = conserver.updateMassConservation(cell, neighbors);

        assertEquals("Unexpected divergence", 0.05, div, EPS);
        assertEquals("Unexpected cell pressure", -11.6, cell.getPressure(), EPS);
        verifyResult(cell, neighbors, new Vector2d(0.875, 0.875),  new Vector2d(1.125, 0.625));
    }

    @Test
    public void testMassConservationForNonUniformFlowSlowNorth() {

        double b0 = 1.0;
        conserver = new MassConserver(b0, DT);

        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0));
        Cell cell = grid.getCell(2, 2);
        cell.setU(0.5); // slower from south
        cell.setStatus(CellStatus.FULL);

        CellNeighbors neighbors = grid.getNeighbors(2, 2);
        double div = conserver.updateMassConservation(cell, neighbors);

        assertEquals("Unexpected divergence", 0.05, div, EPS);
        assertEquals("Unexpected cell pressure", 13.4, cell.getPressure(), EPS);
        verifyResult(cell, neighbors, new Vector2d(0.625, 1.125),  new Vector2d(0.875, 0.875));
    }

    private void verifyResult(Cell cell, CellNeighbors neighbors,
                              Vector2d expUV, Vector2d expBottomLeftUV) {

        assertEquals("Unexpected UV", expUV, new Vector2d(cell.getU(), cell.getV()) );
        Vector2d bottomLeftUV =  new Vector2d(neighbors.getLeft().getU(), neighbors.getBottom().getV());
        assertEquals("Unexpected bottom left UV",
                expBottomLeftUV, bottomLeftUV);
    }

}

