/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1.geometry;

import javax.vecmath.Vector2d;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 *  A snake edge (line geometry) is modeled as a spring to simulate muscles.
 *
 *  @author Barry Becker
 */
public class Edge {

    // the 2 endpoints defining the edge endpoints
    private Particle firstParticle;
    private Particle secondParticle;

    private Line2D.Double segment = null;

    /** constants related the the spring for this edge segment  */
    private static final double K = 0.8; // default  .6

    /** the damping coefficient */
    private static final double D = 1.2; // default

    /** the spring constant K (large K = stiffer) */
    private double k;

    /** damping constant  */
    private double damping;

    /** the resting magnitude of the spring  */
    private double restingLength;

    /** usually the effectiveLength is the same as restingLength except when muscular contraction are happening  */
    private double effectiveLength;

    /** the current magnitude of the spring */
    private double length;

    /** these act like temporary variables for some calculations avoiding many object constructions */
    private final Vector2d direction = new Vector2d();
    private final Vector2d force = new Vector2d();
    private final Vector2d dampingVec = new Vector2d();

    /**
     * Constructor - assumes defaults for the spring constant and damping
     * @param p1 particle that anchors one end of the
     * @param p2 particle that anchors the other end of the edge
     */
    Edge( Particle p1, Particle p2 ) {
        commonInit( p1, p2, K, D );
    }

    public Particle getFirstParticle() {
        return firstParticle;
    }
    public Particle getSecondParticle() {
        return secondParticle;
    }
    public double getRestingLength() {
        return restingLength;
    }
    public double getLength() {
        return length;
    }

    private void commonInit( Particle p1, Particle p2, double k, double d ) {
        segment = new Line2D.Double( p1.x, p1.y, p2.x, p2.y );
        firstParticle = p1;
        secondParticle = p2;

        this.k = k;
        damping = d;
        restingLength = firstParticle.distance(secondParticle);
        effectiveLength = restingLength;
        length = restingLength; // current magnitude
        //System.out.println("firstP = " + firstParticle + " second ="+ secondParticle);
    }

    /**
     *  This method simulates the contraction or expansion of a muscle
     *  the rest magnitude restingLength is effectively changed by the contraction factor.
     *  @param contraction the amount that the spring model for the edge is contracting
     */
    void setContraction( double contraction )  {
        if (contraction <= 0) {
            throw new IllegalArgumentException( "Error contraction <=0 = "+contraction );
            //contraction = EPS;
        }
        effectiveLength = contraction * restingLength;
    }

    /**
     * The force that the spring edge exerts is k times the vector (L-l)p2-p1
     * where L is the resting magnitude of the edge and l is the current magnitude
     * The official formula in proceedings of Siggraph 1988 p169 is
     *   k(L-l) - D* dl/dt
     * @return the computed force exerted on the particle.
     */
    public Vector2d getForce() {
        force.set(secondParticle);
        force.sub(firstParticle);
        direction.set(force);
        direction.normalize();

        // adjust the force by the damping term
        dampingVec.set(secondParticle.velocity);
        dampingVec.sub(firstParticle.velocity);
        double halfEffectiveL = effectiveLength / 2.0;
        double damp = damping * dampingVec.dot(direction);

        length = force.length();
        // never let the force get too great or too small
        if ( length > 2.0 * effectiveLength)
            force.scale( (-k * (effectiveLength - length) * (effectiveLength - length) / effectiveLength - damp) );
        else if ( length < halfEffectiveL ) {
            // prevent the springs from getting too compressed
            double lengthDiff =  restingLength - length;
            force.scale(k * (lengthDiff + 100000.0 * (halfEffectiveL - length)) / halfEffectiveL - damp);
        }
        else {

            //if (d>1.0)
            //   System.out.println("f="+k*(effectiveLength-length)+" - d="+d);
            force.scale( (k * (effectiveLength - length) - damp) );
        }

        return force;
    }

    /**
     * A unit vector in the direction p2-p1
     */
    public Vector2d getDirection() {
        direction.set(secondParticle);
        direction.sub(firstParticle);
        direction.normalize();
        return direction;
    }

    public boolean intersects( Rectangle2D.Double rect ) {
        return segment.intersects( rect );
    }

    /**
     * find the result of taking the dot product of this edge iwth another
     * @param edge to dot this edge with
     * @return the dot product
     */
    public double dot( Edge edge ) {
        return getDirection().dot( edge.getDirection() );
    }
}
