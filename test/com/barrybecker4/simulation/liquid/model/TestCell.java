/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.model;

import com.barrybecker4.simulation.liquid.compute.MassConserver;
import com.barrybecker4.simulation.liquid.compute.SurfaceVelocityUpdater;
import com.barrybecker4.simulation.liquid.compute.VelocityUpdater;
import junit.framework.Assert;
import junit.framework.TestCase;

import javax.vecmath.Vector2d;

/**
 * @author Barry Becker
 */
public class TestCell extends TestCase {

    private static final double VISCOSITY = 0.001;
    private static final  double DT = 0.1;

    public void testCellStatus1() {

        CellBlock cb = new CellBlock();
        Cell cell = cb.get(0,0);

        cb.updateCellStatuses();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.isEmpty());
        cell.incParticles();
        cb.updateCellStatuses();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.isIsolated());
        cell.incParticles();
        cell.incParticles();
        cb.updateCellStatuses();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.isIsolated());
        cb.get(1, 0).incParticles();
        cb.updateCellStatuses();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.isSurface());
        cb.get(-1, 0).incParticles();
        cb.updateCellStatuses();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.isSurface());
        cb.get(0, 1).incParticles();
        cb.updateCellStatuses();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.isSurface());
        cb.get(0, -1).incParticles();
        cb.updateCellStatuses();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.isFull());
    }

    public void testTildeVelocities() {

        CellBlock cb = new CellBlock();
        Cell cell = cb.get(0,0);

        checkTildeVelocities(cb, 0.0, 0.0);

        cell.initializeVelocity(1.0, 0);
        checkTildeVelocities(cb, 1.0, 0.0); // was 1.1, 0.1

        cell.initializeVelocity(0.0, 1.0);
        cell.setPressure(1.0);
        checkTildeVelocities(cb, 0.0, 1.0); // was 0.1, 1.1

        cell.initializeVelocity(1.0, 0);
        cb.setAllCellParticles(5);
        cb.setPressures(1.0);
        cell.setPressure(10.0);
        cb.updateCellStatuses();
        checkTildeVelocities(cb, 1.0, 0.0); //2.1996, 1.0);

        cell.initializeVelocity(1.0, 0);
        cb.setAllCellParticles(5);
        cb.get(0,1).setPressure(0.8);
        cb.get(1,0).initializeVelocity(0.6, 0.3);
        cb.updateCellStatuses();
        checkTildeVelocities(cb, 1.0, 0.0);   // 2.15316, 1.01253);
    }

    private void checkTildeVelocities(CellBlock cb, double expectedU, double expectedV) {
        Cell cell = cb.get(0,0);

        Vector2d force = new Vector2d(1, 1);

        VelocityUpdater updater = new VelocityUpdater();

        updater.updateTildeVelocities(cell,  cb.getCenterNeighbors(),
                                    cb.get(-1,1), cb.get(1,-1),
                                    DT, force, VISCOSITY);
        cell.swap();

        Assert.assertTrue( "Unxepected values Uip=" + cell.getU() + ",  Vjp=" + cell.getV(),
                           (cell.getU() == expectedU) && (cell.getV() == expectedV));
    }

    public void testMassConservation() {

        CellBlock cb = new CellBlock();
        Cell cell = cb.get(0, 0);
        double b = 1.7;
        MassConserver conserver = new MassConserver(b, DT);

        cell.initializeVelocity(1.0, 0);
        cb.setAllCellParticles(5);
        cb.setPressures(1.0);
        cell.setPressure(2.0);
        cb.updateCellStatuses();
        double divergence = conserver.updateMassConservation(cell, cb.getCenterNeighbors());

        Assert.assertEquals("unexpected div="+divergence, 0.0, divergence);

        cell.initializeVelocity(1.0, 0);
        cb.setAllCellParticles(5);
        cb.setPressures(1.0);
        cb.get(1,0).setPressure(2.0);
        cell.setPressure(1.5);
        cb.updateCellStatuses();
        divergence = conserver.updateMassConservation(cell, cb.getCenterNeighbors());

        Assert.assertEquals("unexpected div="+divergence,
                                         0.0, divergence); // 0.1499999999999999
    }


    /**
     * This test dissipateOverflow as well as updateSurfaceVelocities.
     */
    public void testUpdateSurfaceVelocitiesTrivial() {

        double pressure = 1.0;
        CellBlock cb = new CellBlock();
        cb.setPressures(pressure);
        verifySurfaceVelocities(cb, pressure, 1.0, 0.0, 0.0, 0.0, 0.0);
    }

     /**
      * This test dissipateOverflow as well as updateSurfaceVelocities.
      */
    public void testUpdateSurfaceVelocitiesUniformX() {

        double pressure = 1.0;
        CellBlock cb = new CellBlock();
        cb.setPressures(pressure);

        cb.setVelocities(1.0,  0.0);
        verifySurfaceVelocities(cb, pressure,  1.0, 1.0, 1.0, 0.0, 0.0);
    }

    private void verifySurfaceVelocities(CellBlock cb,
                double pressure, double expectedPressure,
                double expRightXVel, double expLeftXVel, double expTopYVel, double expBottomYVel) {

        Cell cell = cb.get(0, 0);

        SurfaceVelocityUpdater updater = new SurfaceVelocityUpdater(pressure);
        updater.updateSurfaceVelocities(cell, cb.getCenterNeighbors());

        Assert.assertEquals("Unexpected pressure.", expectedPressure, cell.getPressure());
        Assert.assertEquals("Unexpected right x velocity.", expRightXVel, cell.getU());
        Assert.assertEquals("Unexpected left x velocity.", expLeftXVel, cb.get(-1, 0).getU());
        Assert.assertEquals("Unexpected top velocity.", expTopYVel,  cell.getV());
        Assert.assertEquals("Unexpected bottom x velocity.", expBottomYVel, cb.get(0, 1).getV());
    }

}
