/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import com.barrybecker4.simulation.liquid.model.*;
import junit.framework.TestCase;
import org.junit.Test;

import javax.vecmath.Vector2d;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Barry Becker
 */
public class PressureUpdaterTest {

    /** delta time */
    private static final double DT = 0.1;
    private static final int DIM = 6;
    private static final double TOL = 0.0000000001;


    /** instance under test. */
    private PressureUpdater pressureUpdater;


    @Test
    public void testPressureUpdateUniform() {

        double b0 = 1.0;
        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(1.0, 1.0), CellStatus.FULL);
        pressureUpdater = new PressureUpdater(grid, b0);

        double maxDiv = pressureUpdater.updatePressure(DT);

        assertEquals("Unexpected divergence", 0.0, maxDiv, TOL);
        assertEquals("Unexpected number of iterations till convergence",
                2, pressureUpdater.getNumIterations());

        Cell cell1 = grid.getCell(1, 1);
        verifyCell(cell1, -49.1, new Vector2d(0.5, 0.5));
        Cell cell2 = grid.getCell(1, 2);
        verifyCell(cell2, 0.9, new Vector2d(1.0, 1.0));
    }


    /** Currently failing */
    @Test
    public void testPressureUpdateNonUniform() {

        double b0 = 1.0;
        Grid grid = new NonUniformGrid(DIM, DIM, new Vector2d(1.0, 1.0), CellStatus.FULL);
        pressureUpdater = new PressureUpdater(grid, b0);

        double maxDiv = pressureUpdater.updatePressure(DT);

        assertEquals("Unexpected divergence", 0.0, maxDiv, 0.000000001);
        assertEquals("Unexpected number of iterations till convergence",
                45, pressureUpdater.getNumIterations());

        /*
        Cell cell1 = grid.getCell(1, 1);
        verifyCell(cell1, -13.385714285714284, new Vector2d(0.14285714285714285, 0.14285714285714285));
        Cell cell2 = grid.getCell(1, 2);
        verifyCell(cell2, 0.9, new Vector2d(0.49120674102731, 0.41098491062604847));
        */
    }

    @Test
    public void testPressureUpdateRandom() {

        double b0 = 1.0;
        Grid grid = new UniformGrid(DIM, DIM, new Vector2d(0.0, 0.0), CellStatus.FULL);

        // insert som wildly varying vectors
        Cell cell22 = grid.getCell(2, 2);
        Cell cell33 = grid.getCell(3, 3);
        cell22.setU(-0.5);
        cell22.setV(0.5);
        cell33.setU(1.5);
        cell33.setV(-0.8);

        pressureUpdater = new PressureUpdater(grid, b0);

        double maxDiv = pressureUpdater.updatePressure(DT);

        assertEquals("Unexpected divergence", 2.7755575615628914E-17, maxDiv, TOL);
        assertEquals("Unexpected number of iterations till convergence",
                2, pressureUpdater.getNumIterations());

        System.out.println(grid.toString());

        Cell cell1 = grid.getCell(1, 1);
        verifyCell(cell1, 0.9, new Vector2d(0.0, 0.0));
        Cell cell2 = grid.getCell(1, 2);
        verifyCell(cell2, 0.9, new Vector2d(0.0, 0.0));
    }

    private void verifyCell(Cell cell, double pressure, Vector2d expUV) {
        assertEquals("Unexpected pressure", pressure, cell.getPressure(), TOL);
        assertEquals("Unexpected velocity", expUV, new Vector2d(cell.getU(), cell.getV()));
    }


    private void verifyPressures(Grid grid, double expPressure) {
        for (int i=1; i<grid.getXDimension()-1; i++) {
            for (int j=1; j<grid.getYDimension()-1; j++) {
                Cell cell = grid.getCell(i, j);

                assertEquals("Unexpected pressure at " + cell,
                        expPressure, cell.getPressure(), TOL);
            }
        }
    }

}

