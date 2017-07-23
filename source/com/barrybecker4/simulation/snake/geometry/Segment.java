/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake.geometry;

import com.barrybecker4.simulation.snake.LocomotionParameters;
import com.barrybecker4.simulation.snake.Snake;

import javax.vecmath.Vector2d;

/**
 *  A segment of a snakes body. It is composed of edges and particles
 *  The structure of the segment looks like this:
 *  <pre>
 *    p3&lt;-------e2&lt;-------p2    left edge
 *    ^  \              / ^
 *    |    e7        e6   |
 *    |      \      /     |     all edges in the middle point to p4
 *    e3       \  /       e1
 *    ^        /p4\       ^
 *    |     /       \     |
 *    |   e4          e5  |
 *    | /               \ |
 *    p0-------&gt;e0-------&gt;p1     right edge
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

    protected double halfLength = 0;
    protected double length = 0;
    private int segmentIndex = 0;

    // keep pointers to the segments in front and in back
    protected Segment segmentInFront = null;
    protected Segment segmentInBack = null;

    protected Edge[] edges = null;
    protected Particle[] particles = null;

    protected double particleMass = 0;

    /** The unit directional spinal vector */
    protected Vector2d direction = new Vector2d( 0, 0 );

    /** temporary vector to aid in calculations (saves creating a lot of new vector objects)  */
    private Vector2d velocityVec = new Vector2d( 0, 0 );
    private Vector2d changeVec = new Vector2d( 0, 0 );

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
        this.length = length;
        halfLength = this.length / 2.0;
        commonInit(width1, width2, (center.x - segmentInFront.getHalfLength() - halfLength),
                   center.y, segmentIndex, snake);

        this.segmentInFront = segmentInFront;
        segmentInFront.segmentInBack = this;

        // reused particles
        particles[1] = segmentInFront.getBackRightParticle();
        particles[2] = segmentInFront.getBackLeftParticle();

        initCommonEdges();
        edges[1] = segmentInFront.getBackEdge();  // front
    }

    public Edge[] getEdges() {
        return edges;
    }

    public Particle[] getParticles() {
        return particles;
    }

    /**
     * Initialize the segment.
     */
    protected void commonInit(double width1, double width2, double xpos, double ypos, int segmentIndex, Snake snake) {
        this.segmentIndex = segmentIndex;

        particles = new Particle[5];
        edges = new Edge[8];

        double segmentMass_ = (width1 + width2) * halfLength;
        particleMass = MASS_SCALE * segmentMass_ / 3;
        double scale = 1.0; //snake.getRenderingParams().getScale();

        particles[0] = new Particle( xpos - halfLength, ypos + scale * width2 / 2.0, particleMass);
        particles[3] = new Particle( xpos - halfLength, ypos - scale * width2 / 2.0, particleMass);
        particles[CENTER_INDEX] = new Particle( xpos, ypos, particleMass);
    }

    protected void initCommonEdges()  {
        edges[0] = new Edge( particles[0], particles[1] ); // bottom (left of snake)
        edges[2] = new Edge( particles[2], particles[3] ); // top (right of snake)
        edges[3] = new Edge( particles[0], particles[3] ); // back

        // inner diagonal edges
        edges[4] = new Edge( particles[0], particles[CENTER_INDEX] );
        edges[5] = new Edge( particles[1], particles[CENTER_INDEX] );
        edges[6] = new Edge( particles[2], particles[CENTER_INDEX] );
        edges[7] = new Edge( particles[3], particles[CENTER_INDEX] );
    }

    public boolean isHead() {
        return (segmentInFront == null);
    }

    public boolean isTail() {
        return (segmentInBack == null);
    }

    private Edge getBackEdge() {
        return edges[3];
    }

    private Particle getBackRightParticle() {
        return particles[0];
    }

    private Particle getBackLeftParticle() {
        return particles[3];
    }

    private Edge getRightEdge() {
        return edges[0];
    }

    private Edge getLeftEdge() {
        return edges[2];
    }

    public Particle getCenterParticle() {
        return particles[CENTER_INDEX];
    }

    private double getHalfLength() {
        return halfLength;
    }

    protected Vector2d getRightForce() {
        return edges[0].getForce();
    }

    protected Vector2d getLeftForce() {
        return edges[2].getForce();
    }

    protected Vector2d getRightBackDiagForce()  {
        return edges[4].getForce();
    }

    protected Vector2d getLeftBackDiagForce() {
        return edges[7].getForce();
    }

    public Vector2d getSpinalDirection()  {
        if ( isTail() ) {
            direction.set( segmentInFront.getCenterParticle().x - particles[CENTER_INDEX].x,
                    segmentInFront.getCenterParticle().y - particles[CENTER_INDEX].y );
        }
        else if ( isHead() ) {
            direction.set( particles[CENTER_INDEX].x - segmentInBack.getCenterParticle().x,
                    particles[CENTER_INDEX].y - segmentInBack.getCenterParticle().y );
        }
        else {
            direction.set( segmentInFront.getCenterParticle().x - segmentInBack.getCenterParticle().x,
                    segmentInFront.getCenterParticle().y - segmentInBack.getCenterParticle().y );
        }
        direction.normalize();
        return direction;
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
        double theta = (double) segmentIndex / period - waveSpeed * time;
        double offset = 0;

        double dir = params.getDirection();
        offset = amplitude * (params.getWaveType().calculateOffset(theta) - dir);

        double contractionLeft = 1.0 + offset;
        double contractionRight = 1.0 - offset;
        if ( contractionRight < 0 ) {
            throw new IllegalArgumentException( "Error contractionRight is less than 0 = " + contractionRight );
            //contractionRight = 0.0;
        }

        edges[0].setContraction( contractionLeft );
        edges[2].setContraction( contractionRight );
    }

    public void translate( Vector2d vec ) {
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                velocityVec.set( particles[i].x, particles[i].y );
                velocityVec.add( vec );
                particles[i].set( velocityVec.x, velocityVec.y );
            }
        }
    }

    /**
     * @return true if either of the edge segments bends to much when compared to its nbr in the next segment
     */
    public boolean isStable() {

        double dot1 = edges[0].dot( segmentInFront.getRightEdge() );
        double dot2 = edges[2].dot( segmentInFront.getLeftEdge() );
        if ( dot1 < MIN_EDGE_ANGLE || dot2 < MIN_EDGE_ANGLE )   {
            System.out.println( "dot1="+dot1+" dot2="+dot2 );
            return false;
        }
        return true;
    }

    public String toString()  {
        StringBuilder str = new StringBuilder( "Segment particles:\n" );
        for ( int i = 0; i < 5; i++ )
            str.append(" p").append(i).append('=').append(particles[i]).append(" \n");
        return str.toString();
    }
}