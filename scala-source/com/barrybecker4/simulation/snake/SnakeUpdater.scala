// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.geometry.SegmentUpdater


/**
  * Makes the snake animate forward one time step.
  * @author Barry Becker
  */
class SnakeUpdater private[snake]() {
  private var segmentUpdater = new SegmentUpdater
  private var snake: Snake = _
  /** the time since the start of the simulation */
  private var time = 0.0
  private val locomotionParams = new LocomotionParameters

  /**
    * steps the simulation forward in time
    * if the timestep is too big inaccuracy and instability will result.
    * @return the new timestep
    */
  def stepForward(snake: Snake, timeStep: Double): Double = {
    this.snake = snake
    updateParticleForces()
    if (locomotionParams.useFriction) updateFrictionalForces()
    updateParticleAccelerations()
    val unstable = updateParticleVelocities(timeStep)
    updateParticlePositions(timeStep)
    time += timeStep
    if (unstable) timeStep / 2
    else 1.0 * timeStep
  }

  /**  Parameters controlling the movement (i.e. locomotion) */
  def getLocomotionParams: LocomotionParameters = locomotionParams

  /** update forces */
  private def updateParticleForces(): Unit = { // apply the sinusoidal muscular contraction function to the
    // left and right sides of the snake
    for (i <- 2 until snake.getNumSegments)
      snake.getSegment(i).contractMuscles(locomotionParams, time)

    // update forces based on surrounding contracted springs
    for (i <- 0 until snake.getNumSegments)
      segmentUpdater.updateForces(snake.getSegment(i))
  }

  /** update accelerations */
  private def updateFrictionalForces() {
    for (i <- 0 until snake.getNumSegments)
      segmentUpdater.updateFrictionalForce(snake.getSegment(i), locomotionParams)
  }

  private def updateParticleAccelerations() {
    for (i <- 0 until snake.getNumSegments)
      segmentUpdater.updateAccelerations(snake.getSegment(i))
  }

  /** @return unstable if velocity changes are getting too big */
  private def updateParticleVelocities(timeStep: Double) = {
    var unstable = false
    for (i <- 0 until snake.getNumSegments)
      if (segmentUpdater.updateVelocities(snake.getSegment(i), timeStep)) unstable = true
    unstable
  }

  /** move particles according to vector field */
  private def updateParticlePositions(timeStep: Double) {
    for (i <- 0 until snake.getNumSegments)
      segmentUpdater.updatePositions(snake.getSegment(i), timeStep)
  }
}