/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.model;

import javax.vecmath.Point2d;

/**
 *  the Particle is the base entity in the liquid simulation.
 *  Ordinarily we would have setter and getter methods for all the data variables
 *  but since this is the lowest level primitive, so performance is tantamount.
 *  Ideas:
 *   - Have varying radii for particles (  private double radius_)
 *   - Have different types of liquids and use for coloration (private int materialType_)
 *
 *  @author Barry Becker
 */
public class Particle extends Point2d {

    /** the cell that the particle belongs to */
    private Cell cell_;

    /**
     * Age of the particle. May be used to color flow by age.
     */
    private double age_;

    /**
     * Construct the particle
     *  assumes that the initial velocity is 0.
     */
    public Particle( double x, double y, Cell cell ) {
        super( x, y );
        cell_ = cell;
        age_ = 0.0;
    }

    public void setCell( Cell cell ) {
        cell_ = cell;
    }

    public Cell getCell() {
        return cell_;
    }

    /**
     * increment the age of the particle
     */
    public void incAge( double timeStep ) {
        age_ += timeStep;
    }

    public double getAge()  {
        return age_;
    }

}
