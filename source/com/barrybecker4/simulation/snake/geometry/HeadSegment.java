/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake.geometry;

import com.barrybecker4.simulation.snake.LocomotionParameters;
import com.barrybecker4.simulation.snake.Snake;

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
     * @param ypos
     */
    public HeadSegment(double width1, double width2, double length, double xpos, double ypos,
                       int segmentIndex, Snake snake) {
        length_ = length;
        halfLength_ = length_ / 2.0;
        commonInit( width1, width2, xpos, ypos, segmentIndex, snake );
        double scale = 1.0; //snake.getRenderingParams().getScale();

        particles_[1] = new Particle( xpos + halfLength_, ypos + scale * width1 / 2.0, particleMass_ );
        particles_[2] = new Particle( xpos + halfLength_, ypos - scale * width1 / 2.0, particleMass_ );

        initCommonEdges();
        edges_[1] = new Edge( particles_[1], particles_[2] ); // front
    }

    /**
     * Intentionally does nothing.
     * There are no muscles to contract in the head since there is no segment in front.
     */
    @Override
    public void contractMuscles( LocomotionParameters params, double time)  {}
}