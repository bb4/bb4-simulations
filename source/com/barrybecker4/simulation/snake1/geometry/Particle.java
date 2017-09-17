/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1.geometry;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 *  Particle is a point mass that approximates the mass of the snake.
 *  It is also a vertex composing the geometry.
 *  No getter/setter because we need speed.
 *
 *  @author Barry Becker
 */
public class Particle extends Point2d {

    /** the velocity vector of the particle in m/s */
    public Vector2d velocity;

    /** the acceleration vector of the particle in m/s^2 */
    Vector2d acceleration;

    /** the force vector (sum of all forces acting on the particle) */
    public Vector2d force;

    /** the frictional force if used */
    public Vector2d frictionalForce;

    /** the mass of the particle in kg*/
    public double mass = 0;

    /**
     * Construct the particle
     * assumes that the initial velocity is 0.
     */
    public Particle( double x, double y, double m ) {
        super( x, y );
        velocity = new Vector2d( 0.0, 0.0);
        acceleration = new Vector2d( 0.0, 0.0 );
        force = new Vector2d( 0.0, 0.0 );
        frictionalForce = new Vector2d( 0.0, 0.0 );
        mass = m;
    }
}
