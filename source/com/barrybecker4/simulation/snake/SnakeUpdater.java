// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake;

import com.barrybecker4.simulation.snake.geometry.SegmentUpdater;

/**
 *  Makes the snake animate forward one time step.
 *
 *  @author Barry Becker
 */
public class SnakeUpdater {

    private Snake snake;

    /** the time since the start of the simulation */
    private double time_ = 0.0;

    private LocomotionParameters locomotionParams = new LocomotionParameters();
    private SegmentUpdater segmentUpdater;

    /**
     * Constructor
     */
    public SnakeUpdater() {
        segmentUpdater = new SegmentUpdater();
    }

    /**
     * steps the simulation forward in time
     * if the timestep is too big inaccuracy and instability will result.
     * @return the new timestep
     */
    public double stepForward(Snake snake, double timeStep )  {

        this.snake = snake;
        updateParticleForces();

        if ( locomotionParams.getUseFriction() ) {
            updateFrictionalForces();
        }
        updateParticleAccelerations();
        boolean unstable = updateParticleVelocities( timeStep );
        updateParticlePositions( timeStep );

        time_ += timeStep;
        if ( unstable ) {
            return timeStep / 2;
        }
        else {
            return 1.0 * timeStep;
        }
    }

    /**
     * Parameters controlling the movement (i.e. locomotion)
     */
    public LocomotionParameters getLocomotionParams() {
        return locomotionParams;
    }

    /**
     * update forces
     */
    private void updateParticleForces() {

        // apply the sinusoidal muscular contraction function to the
        // left and right sides of the snake
        for ( int i = 2; i <snake.getNumSegments(); i++ )
            snake.getSegment(i).contractMuscles(locomotionParams, time_);

        // update forces based on surrounding contracted springs
        for ( int i = 0; i < snake.getNumSegments(); i++ )
            segmentUpdater.updateForces(snake.getSegment(i));
    }

    /**
     * update accelerations
     */
    private void updateFrictionalForces() {
        for ( int i = 0; i < snake.getNumSegments(); i++ )
            segmentUpdater.updateFrictionalForce(snake.getSegment(i),  locomotionParams);
    }

    /**
     * update accelerations
     */
    private void updateParticleAccelerations() {
        for ( int i = 0; i < snake.getNumSegments(); i++ )
            segmentUpdater.updateAccelerations( snake.getSegment(i));
    }

    /**
     * update velocities
     * @return unstable if velocity changes are getting too big
     */
    private boolean updateParticleVelocities( double timeStep ) {
        boolean unstable = false;
        for ( int i = 0; i < snake.getNumSegments(); i++ )
            if ( segmentUpdater.updateVelocities( snake.getSegment(i), timeStep) )
                unstable = true;
        return unstable;
    }

    /**
     * move particles according to vector field
     */
    private void updateParticlePositions( double timeStep ) {
        for ( int i = 0; i < snake.getNumSegments(); i++ )  {
            segmentUpdater.updatePositions(snake.getSegment(i), timeStep);
        }
    }
}
