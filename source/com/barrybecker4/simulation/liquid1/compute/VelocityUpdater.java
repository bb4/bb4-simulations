/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.compute;

import com.barrybecker4.simulation.liquid1.model.Cell;
import com.barrybecker4.simulation.liquid1.model.CellDimensions;
import com.barrybecker4.simulation.liquid1.model.CellNeighbors;

import javax.vecmath.Vector2d;

/**
 *  Updates the velocities in the full regions of the liquid (i.e. not on the surface).
 *
 *  @author Barry Becker
 */
public class VelocityUpdater {

    /** size of a cell */
    private final CellDimensions dims = new CellDimensions();

    /**
     * compute velocity at next time step given neighboring cells.
     *      cXm1Yp1   nb.top
     *     nb.left      M      nb.right
     *        0     nb.bottom   cXp1Ym1
     *
     * The formulas here equate to a numerical solution
     * of the Navier-Stokes equation.
     * RISK:5
     */
    public void updateTildeVelocities(Cell cell, CellNeighbors neighbors,
                                      Cell cXm1Yp1, Cell cXp1Ym1,
                                      double dt, Vector2d force, double viscosity ) {

        // if not FULL, then the old velocity will be the new.
        if ( !cell.isFull() ) {
            cell.passVelocityThrough();
            return;
        }
        assert  (dt > 0.0000001) : "dt got too small";

        // u(i+0.5, j+0.5) = 0.5*(u(i+0.5, j) + u(i+0.5, j+1))
        double u_ipjp = (cell.getU() + neighbors.getTop().getU()) / 2.0;

        // v(i+0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i+1, j+0.5))
        double v_ipjp = (cell.getV() + neighbors.getRight().getV()) / 2.0;

        double ipjp2 = u_ipjp * v_ipjp;

        calcUTilde(cell, neighbors, cXp1Ym1.getV(), ipjp2, dt, force.x, viscosity);
        calcVTilde(cell, neighbors, cXm1Yp1.getU(), ipjp2, dt, force.y, viscosity);
    }

    /**
     * Update new U component of velocity.
     */
    private void calcUTilde(Cell cell, CellNeighbors neighbors, double lowerRightV, double ipjp2,
                            double dt, double forceX,  double viscosity ) {

        // u(i, j) = 0.5*(u(i+0.5, j) + u(i-0.5, j))
        double u_i = (cell.getU() + neighbors.getLeft().getU()) / 2.0;

        // u(i+1, j) = 0.5*(u(i+1.5, j) + u(i+0.5, j))
        double u_ip1 = (neighbors.getRight().getU() + cell.getU()) / 2.0;

        // u(i+0.5, j-0.5) = 0.5*(u(i+0.5, j) + u(i+0.5, j-1))
        double u_ipjm = (cell.getU() + neighbors.getBottom().getU()) / 2.0;

        // v(i+0.5, j-0.5) = 0.5*(v(i, j-0.5) + v(i+1, j-0.5))
        double v_ipjm = (neighbors.getBottom().getV() + lowerRightV) / 2.0;

        if ( !neighbors.getRight().isObstacle() ) {
            double xNume = (u_i * u_i  -  u_ip1 * u_ip1);
            double yNume = (u_ipjm * v_ipjm  -  ipjp2);
            double v1 = (neighbors.getRight().getU() - 2 * cell.getU()
                                 + neighbors.getLeft().getU()) / dims.dxSq;
            double v2 = (neighbors.getTop().getU() - 2 * cell.getU()
                                 + neighbors.getBottom().getU()) / dims.dySq;
            double pf = xNume / dims.dx + yNume / dims.dy + forceX
                               + (cell.getPressure() + neighbors.getLeft().getPressure()) / dims.dx
                               + viscosity * (v1 + v2);
            double newu = cell.getU() + dt * pf;
            /*
            if (Math.abs(pf) > 10) {
                System.out.println("much bigger x change than expected. oldu ="
                  + uip_[current_] + " newu="+ newu
                        + " forceX=" + forceX + " forceY="+forceY
                        + "\ncXp1=" + cXp1 + " cXm1=" + cXm1
                        + "\ncXp1=" + cYp1 + " cXm1=" + cYm1
                        + "\ncXp1Ym1=" + cXp1Ym1 + " cXm1Yp1=" + cXm1Yp1);
            } */
            cell.setNewU(newu);
        }
    }

    /**
     * Update new V component of velocity.
     */
    private void calcVTilde(Cell cell, CellNeighbors neighbors, double upperLeftU, double ipjp2,
                           double dt, double forceY,  double viscosity) {

        // u(i-0.5, j+0.5) = 0.5*(u(i-0.5, j) + u(i-0.5, j+1))
        double u_imjp = (neighbors.getLeft().getU() + upperLeftU) / 2.0;

        // v(i-0.5, j+0.5) = 0.5*(v(i, j+0.5) + v(i-1, j+0.5))
        double v_imjp = (cell.getV() + neighbors.getBottom().getV()) / 2.0;

        // v(i, j) = 0.5*(v(i, j-0.5) + v(i, j+0.5))
        double v_j = (neighbors.getBottom().getV() + cell.getV()) / 2.0;

        // v(i, j+1) = 0.5*(v(i, j+0.5) + v(i, j+1.5) // / 2.0 was not here originally
        double v_jp1 = (cell.getV() + neighbors.getTop().getV()) / 2.0;

        if ( !neighbors.getTop().isObstacle() ) {
            double xNume = (u_imjp * v_imjp - ipjp2);
            double yNume = (v_j * v_j - v_jp1 * v_jp1);
            double v1 =  (neighbors.getRight().getV() - 2 * cell.getV()
                       + neighbors.getLeft().getV()) / dims.dxSq;
            double v2 =  (neighbors.getTop().getV() - 2 * cell.getV()
                       + neighbors.getBottom().getV()) / dims.dySq;
            double pf = xNume / dims.dx + yNume / dims.dy + forceY
                    + (cell.getPressure() - neighbors.getBottom().getPressure()) / dims.dy
                    + viscosity * (v1 + v2);
            double newv = cell.getV() + dt * pf;
            /*
            if (Math.abs(pf) > 5.0) {
                System.out.println("much bigger y change than expected. oldv ="
                      + vjp_[current_] + " newv="+ newv
                        + "\nforceX=" + forceX + " forceY="+forceY
                        + "\ncXp1=" + cXp1 + " cXm1=" + cXm1
                        + "\ncXp1=" + cYp1 + " cXm1=" + cYm1
                        + "\ncXp1Ym1=" + cXp1Ym1 + " cXm1Yp1=" + cXm1Yp1);
            } */
            cell.setNewV(newv);
        }
    }
}