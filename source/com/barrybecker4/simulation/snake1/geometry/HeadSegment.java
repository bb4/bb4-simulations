/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake1.geometry;

import com.barrybecker4.simulation.snake1.LocomotionParameters;

/**
 *  The head/nose segment of the snakes body.
 *  @author Barry Becker
 */
public class HeadSegment extends Segment {

    /**
     * Constructor
     * @param width1 the width of the segment that is nearest the nose
     * @param width2 the width of the segment nearest the tail
     * @param xpos position of the center of the segment
     * @param ypos position of the center of the segment
     */
    public HeadSegment(double width1, double width2, double length, double xpos, double ypos,
                       int segmentIndex) {
        this.length = length;
        halfLength = this.length / 2.0;
        commonInit(width1, width2, xpos, ypos, segmentIndex);
        double scale = 1.0; //snake.getRenderingParams().getScale();

        particles[1] = new Particle( xpos + halfLength, ypos + scale * width1 / 2.0, particleMass);
        particles[2] = new Particle( xpos + halfLength, ypos - scale * width1 / 2.0, particleMass);

        initCommonEdges();
        edges[1] = new Edge( particles[1], particles[2] ); // front
    }

    /**
     * Intentionally does nothing.
     * There are no muscles to contract in the head since there is no segment in front.
     */
    @Override
    public void contractMuscles( LocomotionParameters params, double time)  {}
}