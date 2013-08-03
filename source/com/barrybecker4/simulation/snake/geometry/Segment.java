/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake.geometry;

import com.barrybecker4.simulation.snake.LocomotionParameters;
import com.barrybecker4.simulation.snake.Snake;

import javax.vecmath.Vector2d;

/**
 *  A segment of a snakes body. It is composed of edges and particles
 *  The structure of the segment looks like this:
 *  <pre>
 *    p3<-------e2<-------p2    left edge
 *    ^  \              / ^
 *    |    e7        e6   |
 *    |      \      /     |     all edges in the middle point to p4
 *    e3       \  /       e1
 *    ^        /p4\       ^
 *    |     /       \     |
 *    |   e4          e5  |
 *    | /               \ |
 *    p0------->e0------->p1     right edge
 *  </pre>
 *
 *  @author Barry Becker
 */
public class Segment {

    /** Edge angles are not allowed to become less than this - to prevent instability. */
    private  static final double MIN_EDGE_ANGLE = 0.3;

    /** number of particles per segment (2 of which are shared between segments)  */
    static final int NUM_PARTICLES = 5;

    /** index of the center particle */
    static final int CENTER_INDEX = 4;

    private static final double EPS = 0.00001;
    private static final double MASS_SCALE = 1.0;

    protected double halfLength_ = 0;
    protected double length_ = 0;
    private int segmentIndex_ = 0;

    // keep pointers to the segments in front and in back
    protected Segment segmentInFront_ = null;
    protected Segment segmentInBack_ = null;

    protected Edge[] edges_ = null;
    protected Particle[] particles_ = null;

    protected double particleMass_ = 0;

    /** The unit directional spinal vector */
    protected Vector2d direction_ = new Vector2d( 0, 0 );

    /** temporary vector to aid in calculations (saves creating a lot of new vector objects)  */
    private Vector2d vel_ = new Vector2d( 0, 0 );
    private Vector2d change_ = new Vector2d( 0, 0 );

    protected Segment() {}

    /**
     * constructor for all segments but the nose
     * @param width1 the width of the segment that is nearest the nose
     * @param width2 the width of the segment nearest the tail
     * @param segmentInFront the segment in front of this one
     */
    public Segment(double width1, double width2, double length,
                   Segment segmentInFront, int segmentIndex, Snake snake ) {

        Particle center = segmentInFront.getCenterParticle();
        length_ = length;
        halfLength_ = length_ / 2.0;
        commonInit(width1, width2, (center.x - segmentInFront.getHalfLength() - halfLength_),
                   center.y, segmentIndex, snake);

        segmentInFront_ = segmentInFront;
        segmentInFront.segmentInBack_ = this;

        // reused particles
        particles_[1] = segmentInFront.getBackRightParticle();
        particles_[2] = segmentInFront.getBackLeftParticle();

        initCommonEdges();
        edges_[1] = segmentInFront.getBackEdge();  // front
    }

    public Edge[] getEdges() {
        return edges_;
    }

    public Particle[] getParticles() {
        return particles_;
    }

    /**
     * Initialize the segment.
     */
    protected void commonInit(double width1, double width2, double xpos, double ypos, int segmentIndex, Snake snake) {
        segmentIndex_ = segmentIndex;

        particles_ = new Particle[5];
        edges_ = new Edge[8];

        double segmentMass_ = (width1 + width2) * halfLength_;
        particleMass_ = MASS_SCALE * segmentMass_ / 3;
        double scale = 1.0; //snake.getRenderingParams().getScale();

        particles_[0] = new Particle( xpos - halfLength_, ypos + scale * width2 / 2.0, particleMass_ );
        particles_[3] = new Particle( xpos - halfLength_, ypos - scale * width2 / 2.0, particleMass_ );
        particles_[CENTER_INDEX] = new Particle( xpos, ypos, particleMass_ );
    }

    protected void initCommonEdges()  {
        edges_[0] = new Edge( particles_[0], particles_[1] ); // bottom (left of snake)
        edges_[2] = new Edge( particles_[2], particles_[3] ); // top (right of snake)
        edges_[3] = new Edge( particles_[0], particles_[3] ); // back

        // inner diagonal edges
        edges_[4] = new Edge( particles_[0], particles_[CENTER_INDEX] );
        edges_[5] = new Edge( particles_[1], particles_[CENTER_INDEX] );
        edges_[6] = new Edge( particles_[2], particles_[CENTER_INDEX] );
        edges_[7] = new Edge( particles_[3], particles_[CENTER_INDEX] );
    }

    public boolean isHead() {
        return (segmentInFront_ == null);
    }

    public boolean isTail() {
        return (segmentInBack_ == null);
    }

    private Edge getBackEdge() {
        return edges_[3];
    }

    private Particle getBackRightParticle() {
        return particles_[0];
    }

    private Particle getBackLeftParticle() {
        return particles_[3];
    }

    private Edge getRightEdge() {
        return edges_[0];
    }

    private Edge getLeftEdge() {
        return edges_[2];
    }

    public Particle getCenterParticle() {
        return particles_[CENTER_INDEX];
    }

    private double getHalfLength() {
        return halfLength_;
    }

    protected Vector2d getRightForce() {
        return edges_[0].getForce();
    }

    protected Vector2d getLeftForce() {
        return edges_[2].getForce();
    }

    protected Vector2d getRightBackDiagForce()  {
        return edges_[4].getForce();
    }

    protected Vector2d getLeftBackDiagForce() {
        return edges_[7].getForce();
    }

    public Vector2d getSpinalDirection()  {
        if ( isTail() ) {
            direction_.set( segmentInFront_.getCenterParticle().x - particles_[CENTER_INDEX].x,
                    segmentInFront_.getCenterParticle().y - particles_[CENTER_INDEX].y );
        }
        else if ( isHead() ) {
            direction_.set( particles_[CENTER_INDEX].x - segmentInBack_.getCenterParticle().x,
                    particles_[CENTER_INDEX].y - segmentInBack_.getCenterParticle().y );
        }
        else {
            direction_.set( segmentInFront_.getCenterParticle().x - segmentInBack_.getCenterParticle().x,
                    segmentInFront_.getCenterParticle().y - segmentInBack_.getCenterParticle().y );
        }
        direction_.normalize();
        return direction_;
    }

    /**
     * Contract the muscles on the left and right of the segment.
     * Don't contract the nose because there are no muscles there
     */
    public void contractMuscles(LocomotionParameters params, double time)  {

        double waveSpeed = params.getWaveSpeed();
        double amplitude = params.getWaveAmplitude();
        double period = params.getWavePeriod();

        //Vector2d muscleForce = v;
        double theta = (double) segmentIndex_ / period - waveSpeed * time;
        double offset = 0;

        double dir = params.getDirection();
        offset = amplitude * (params.getWaveType().calculateOffset(theta) - dir);

        double contractionLeft = 1.0 + offset;
        double contractionRight = 1.0 - offset;
        if ( contractionRight < 0 ) {
            throw new IllegalArgumentException( "Error contractionRight is less than 0 = " + contractionRight );
            //contractionRight = 0.0;
        }

        edges_[0].setContraction( contractionLeft );
        edges_[2].setContraction( contractionRight );
    }

    public void translate( Vector2d vec ) {
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                vel_.set( particles_[i].x, particles_[i].y );
                vel_.add( vec );
                particles_[i].set( vel_.x, vel_.y );
            }
        }
    }

    /**
     * @return true if either of the edge segments bends to much when compared to its nbr in the next segment
     */
    public boolean isStable() {

        double dot1 = edges_[0].dot( segmentInFront_.getRightEdge() );
        double dot2 = edges_[2].dot( segmentInFront_.getLeftEdge() );
        if ( dot1 < MIN_EDGE_ANGLE || dot2 < MIN_EDGE_ANGLE )   {
            System.out.println( "dot1="+dot1+" dot2="+dot2 );
            return false;
        }
        return true;
    }

    public String toString()  {
        StringBuilder str = new StringBuilder( "Segment particles:\n" );
        for ( int i = 0; i < 5; i++ )
            str.append(" p").append(i).append('=').append(particles_[i]).append(" \n");
        return str.toString();
    }
}