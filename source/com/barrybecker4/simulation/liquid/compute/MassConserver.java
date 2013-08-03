/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import com.barrybecker4.simulation.liquid.model.Cell;
import com.barrybecker4.simulation.liquid.model.CellDimensions;
import com.barrybecker4.simulation.liquid.model.CellNeighbors;

/**
 *  Ensures that mass is conserved while processing.
 *
 *  @author Barry Becker
 */
public class MassConserver {

    /** size of a cell */
    private final CellDimensions dims = new CellDimensions();

    private double b0;
    private double dt;

    /**
     * constructor
     * @param b0 relaxation coefficient.
     * @param dt delta time.
     */
    public MassConserver(double b0, double dt)  {

        this.b0 = b0;
        this.dt = dt;
    }

    /**
     * Update pressure and velocities to satisfy mass conservation.
     * What is the intuitive meaning of b0?
     * RISK:3
     * @return the amount of divergence from the cell that
     * we will need to dissipate.
     */
    public double updateMassConservation(Cell cell, CellNeighbors neighbors) {

        if ( !cell.isFull() ) {
            return 0;
        }

        // divergence of fluid within the cell.
        double divergence =
                (neighbors.getLeft().getU() - cell.getU()) / dims.dx +
                (neighbors.getBottom().getV() - cell.getV()) / dims.dy;

        double b = b0 / (dt * (2.0 / dims.dxSq + 2.0 / dims.dySq));

        // the change in pressure for a cell.
        double dp = b * divergence;
        double dpdx = dt * dp / dims.dx;
        double dpdy = dt * dp / dims.dy;

        if ( !neighbors.getRight().isObstacle() ) {
            cell.incrementU(dpdx);
        }

        if ( !neighbors.getLeft().isObstacle() ) {
            neighbors.getLeft().incrementU(-dpdx);
        }

        if ( !neighbors.getTop().isObstacle() ) {
            cell.incrementV(dpdy);
        }

        if ( !neighbors.getBottom().isObstacle() ) {
            neighbors.getBottom().incrementV(-dpdy);
        }

        cell.setPressure(cell.getPressure() + dp);
        return Math.abs( divergence );
    }
}