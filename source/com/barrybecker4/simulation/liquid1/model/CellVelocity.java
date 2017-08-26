/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.model;

/**
 *  Keeps track of the current and last velocities for the cell.
 *  The u component heads out of the cell to the left, and
 *  the v component heads out of the cell to the top.
 *  velocities in x, y directions are defined at the center of each face.
 *
 *  @author Barry Becker
 */
public class CellVelocity {

    /** uip = u(i+0.5, j, k) */
    private final double[] uip = new double[2];

    /** vjp = v(i, j+0.5, k) */
    private final double[] vjp = new double[2];

    /** use this to switch between current and last copies of fields. (hack) */
    private int current;

    /**
     * Constructor
     */
    CellVelocity()  {
        uip[0] = uip[1] = 0;
        vjp[0] = vjp[1] = 0;
    }

    /**
     *  global swap of fields (use with care). (hack)
     */
    public void step()  {
        current = 1 - current;
    }

    void passThrough() {
        uip[1 - current] = uip[current]; // + dt * forceX;
        vjp[1 - current] = vjp[current]; // + dt * forceY;
    }

    void initializeU(double u) {
        uip[0] = u;
        uip[1] = u;
    }
    void initializeV(double v) {
        vjp[0] = v;
        vjp[1] = v;
    }

    public double getU() {
        return uip[current];
    }
    public double getV() {
        return vjp[current];
    }

    void setCurrentU(double u) {
        uip[current] = u;
    }

    void setCurrentV(double v) {
        vjp[current] = v;
    }

    void setNewU(double newu) {
        uip[1 - current] = newu;
    }

    void setNewV(double newv) {
        vjp[1 - current] = newv;
    }

    void incrementU(double inc) {
        uip[current] += inc;
    }

    void incrementV(double inc) {
        vjp[current] += inc;
    }

    public void initialize(double u, double v) {
        uip[0] = u;
        uip[1] = u;
        vjp[0] = v;
        vjp[1] = v;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("velocity=(");
        sb.append(uip[current]).append(", ").append(vjp[current]).append(")");
        return sb.toString();
    }
}