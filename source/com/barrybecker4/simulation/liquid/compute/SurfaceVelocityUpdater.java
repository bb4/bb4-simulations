/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.compute;

import com.barrybecker4.simulation.liquid.model.Cell;
import com.barrybecker4.simulation.liquid.model.CellDimensions;
import com.barrybecker4.simulation.liquid.model.CellNeighbors;

/**
 * Update the velocities for surface cells
 * There are 13 surface conditions.
 *   1-4) One of the 4 immediate neighbors is empty
 *   5-8) Two of the 4 immediate neighbors is empty
 *   9-12) Three of the 4 immediate neighbors is empty
 *   13) isolated
 *
 *  @author Barry Becker
 */
public class SurfaceVelocityUpdater {

    /**  4 if 2d, 6 if 3d */
    private static final int NUM_CELL_FACES = 4;

    /** size of a cell */
    private final CellDimensions dims = new CellDimensions();

    private double pressure0;

    /**
     * Constructor
     * @param pressure0 base pressure to set after dissipating overflow.
     */
    public SurfaceVelocityUpdater(double pressure0)  {

        this.pressure0 = pressure0;
    }

    /**
     * Force no divergence in surface cells, by updating velocities directly.
     * Any overflow will be dissipated.
     * @param neighbors the cell's immediate neighbors
     * RISK:1
     */
    public void updateSurfaceVelocities(Cell cell, CellNeighbors neighbors) {

        // only surface cells can have overflow dissipated.
        if ( !(cell.isSurface() || cell.isIsolated()) ) {
            return;
        }

        int count = 0;
        double overflow = 0;

        if ( !neighbors.getRight().isEmpty() ) {
            count++;
            overflow += cell.getU() / dims.dx;
        }

        if ( !neighbors.getLeft().isEmpty() ) {
            count++;
            overflow -= neighbors.getLeft().getU() / dims.dx;
        }

        if ( !neighbors.getTop().isEmpty() ) {
            count++;
            overflow += cell.getV() / dims.dy;
        }

        if ( !neighbors.getBottom().isEmpty() ) {
            count++;
            overflow -= neighbors.getBottom().getV() / dims.dy;
        }

        if ( count < NUM_CELL_FACES && Math.abs( overflow ) > 0.0 ) {
            dissipateOverflow(cell, (NUM_CELL_FACES - count), overflow, neighbors );
        }

        cell.setPressure(pressure0);
    }

    /**
     * Ensure that what comes in must also go out.
     * cXp1 stands for the neighbor cell that is located at +1 in X direction.
     * The overflow is equally distributed to the open adjacent surfaces.
     * @param numSurfaces number of empty adjacent cells. In other
     *     words the number of surfaces we have.
     * @param overflow the overflow to dissipate out the
     *     surface sides that do not have liquid.
     * RISK:3
     */
    private void dissipateOverflow(Cell cell, int numSurfaces, double overflow,
                                   CellNeighbors neighbors) {

        if (Math.abs(overflow) > 100) {
            System.out.println("dissipating large overflow ="+overflow);
        }

        int count = 0;
        double overflowX = dims.dx * overflow / numSurfaces;
        double overflowY = dims.dy * overflow / numSurfaces;

        if ( neighbors.getRight().isEmpty() ) {
            count++;
            cell.setU(-overflowX);
        }
        if (neighbors.getLeft().isEmpty() ) {
            count++;
            neighbors.getLeft().setU(overflowX);
        }

        if ( neighbors.getTop().isEmpty() ) {
            count++;
            cell.setV(-overflowY);
        }
        if ( neighbors.getBottom().isEmpty() ) {
            count++;
            neighbors.getBottom().setV(overflowY);
        }
        assert (count == numSurfaces);
    }

}