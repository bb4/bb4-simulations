/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.model;

/**
 *  Keeps track of the current and last velocities for the cell.
 *  The u component heads out of the cell to the left, and
 *  the v component heads out of the cell to the top.
 *  velocities in x, y directions are defined at the center of each face.
 *
 *  @author Barry Becker
 */
public class CellVelocity {

    /** uip_ = u(i+0.5, j, k) */
    private final double[] uip_ = new double[2];

    /** vjp_ = v(i, j+0.5, k) */
    private final double[] vjp_ = new double[2];

    /** use this to switch between current and last copies of fields. (hack) */
    private int current_;

    /**
     * Constructor
     */
    public CellVelocity()  {
        uip_[0] = uip_[1] = 0;
        vjp_[0] = vjp_[1] = 0;
    }

    /**
     *  global swap of fields (use with care). (hack)
     */
    public void step()  {
        current_ = 1 - current_;
    }

    public void passThrough() {
        uip_[1 - current_] = uip_[current_]; // + dt * forceX;
        vjp_[1 - current_] = vjp_[current_]; // + dt * forceY;
    }

    public void initializeU(double u) {
        uip_[0] = u;
        uip_[1] = u;
    }
    public void initializeV(double v) {
        vjp_[0] = v;
        vjp_[1] = v;
    }

    public double getU() {
        return uip_[current_];
    }
    public double getV() {
        return vjp_[current_];
    }

    public void setCurrentU(double u) {
        uip_[current_] = u;
    }

    public void setCurrentV(double v) {
        vjp_[current_] = v;
    }

    public void setNewU(double newu) {
        uip_[1 - current_] = newu;
    }

    public void setNewV(double newv) {
        vjp_[1 - current_] = newv;
    }

    public void incrementU(double inc) {
        uip_[current_] += inc;
    }

    public void incrementV(double inc) {
        vjp_[current_] += inc;
    }

    public void initialize(double u, double v) {
        uip_[0] = u;
        uip_[1] = u;
        vjp_[0] = v;
        vjp_[1] = v;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("velocity=(");
        sb.append(uip_[current_]).append(", ").append(vjp_[current_]).append(")");
        return sb.toString();
    }
}