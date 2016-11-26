/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.model;

import static com.barrybecker4.simulation.common.PhysicsConstants.ATMOSPHERIC_PRESSURE;

/**
 *  A region of space containing particles, walls, or liquid.
 *  Adapted from work by Nick Foster.
 *
 *               ^  vjp_[cur_]     (positive v direction)
 *               |
 *          _____________
 *         |            |
 *    &lt;--  |      p     |  --&gt; uip_[cur_]   (positive u dir)
 *         |            |
 *          ------------
 *               |
 *               v
 *
 *          |--- dx ---|
 *
 *  @author Barry Becker
 */
public class Cell {

    /** type of cell  */
    private CellStatus status;

    /** pressure at the center of the cell */
    private double pressure;

    /** velocities in x, y directions defined at the center of each face */
    private CellVelocity velocity;

    /** Number of particles in this cell */
    private int numParticles;

    /**
     * constructor
     */
    public Cell()  {
        pressure = ATMOSPHERIC_PRESSURE;
        velocity = new CellVelocity();

        numParticles = 0;
        status = CellStatus.EMPTY;
    }

    /**
     *  global swap of fields (use with care). (hack)
     */
    public void swap()  {
        velocity.step();
    }

    public void setPressure( double p ) {
        pressure = p;
    }
    public double getPressure() {
        return pressure;
    }

    public void initializeU(double u) {
        velocity.initializeU(u);
    }
    public void initializeV(double v) {
         velocity.initializeV(v);
    }

    public void initializeVelocity(double u, double v) {
        velocity.initialize(u, v);
    }

    public void setU(double u) {
        velocity.setCurrentU(u);
    }

    public void setV(double v) {
        velocity.setCurrentV(v);
    }

    public double getU() {
        return velocity.getU();
    }
    public double getV() {
       return velocity.getV();
    }

    public void incrementU(double inc) {
        velocity.incrementU(inc);
    }
    public void incrementV(double inc) {
        velocity.incrementV(inc);
    }

    public void setNewU(double u) {
        velocity.setNewU(u);
    }
    public void setNewV(double v) {
        velocity.setNewV(v);
    }

    public void passVelocityThrough() {
        velocity.passThrough();
    }

    public void incParticles() {
        numParticles++;
    }
    public void decParticles() {
        numParticles--;
        assert numParticles >= 0;
    }

    public int getNumParticles() {
        return numParticles;
    }

    public CellStatus getStatus() {
        return status;
    }

    public boolean isSurface() {
        return status == CellStatus.SURFACE;
    }
    public boolean isEmpty() {
        return status == CellStatus.EMPTY;
    }
    public boolean isFull() {
        return status == CellStatus.FULL;
    }
    public boolean isObstacle() {
        return status == CellStatus.OBSTACLE;
    }
    public boolean isIsolated() {
        return status == CellStatus.ISOLATED;
    }

    public void setStatus( CellStatus status ) {
        this.status = status;
    }

    /**
     * Compute the cell's new status based on numParticles inside and
     * the status of neighbors.
     * RISK: 1
     */
    public void updateStatus( CellNeighbors neighbors ) {

        assert (numParticles >= 0) : "num particles less than 0.";

        if ( status == CellStatus.OBSTACLE ) {
            // obstacles never change status
        }
        else if ( numParticles == 0 ) {
            status = CellStatus.EMPTY;
        }
        else {
            if ( neighbors.allHaveParticles() ) {
                status = CellStatus.FULL;
            }
            else if (neighbors.noneHaveParticles() ) {
                // warning: not present in original foster code.
                status = CellStatus.ISOLATED;
            }
            else {
                status = CellStatus.SURFACE;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cell:");
        sb.append(status);
        sb.append(" num particles=").append(numParticles);
        sb.append(" pressure=").append(pressure);
        sb.append(" ").append(velocity);
        return sb.toString();
    }
}