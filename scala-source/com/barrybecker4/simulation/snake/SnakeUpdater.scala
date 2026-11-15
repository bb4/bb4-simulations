// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.simulation.snake.geometry.SegmentUpdater
import scala.compiletime.uninitialized


object SnakeUpdater {
  /**
    * Hard cap on a single Euler step. The UI slider can request larger values, but integrating
    * with a huge dt teleports particles even when velocity updates are skipped.
    */
  private[snake] val MaxTimeStep = 0.25
}

/**
  * Makes the snake animate forward one time step.
  */
class SnakeUpdater private[snake]() {
  private var segmentUpdater = new SegmentUpdater
  private var snake: Snake = uninitialized
  /** the time since the start of the simulation */
  private var time = 0.0
  private val locomotionParams = new LocomotionParameters

  /**
    * steps the simulation forward in time
    * if the timestep is too big inaccuracy and instability will result.
    * @return the new timestep (may be reduced when the step was unstable or clamped)
    */
  def stepForward(snake: Snake, timeStep: Double): Double = {
    this.snake = snake
    val dt = math.min(timeStep, SnakeUpdater.MaxTimeStep)
    snake.syncParticleMassesFromLocomotionParams()
    updateParticleForces()
    if (locomotionParams.useFriction) updateFrictionalForces()
    updateParticleAccelerations()
    val unstable = updateParticleVelocities(dt)
    // Do not integrate positions for an unstable velocity step — that was blowing the snake apart
    // when the timestep slider jumped upward mid-run.
    if (!unstable) {
      updateParticlePositions(dt)
      time += dt
    }
    if (unstable) dt / 2
    else dt
  }

  /**  Parameters controlling the movement (i.e. locomotion) */
  def getLocomotionParams: LocomotionParameters = locomotionParams

  /** Reset the muscular contraction clock (used in [[contractMuscles]]); call when restoring initial snake state. */
  private[snake] def resetSimulationTime(): Unit = {
    time = 0.0
  }

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
  private def updateFrictionalForces(): Unit = {
    for (i <- 0 until snake.getNumSegments)
      segmentUpdater.updateFrictionalForce(snake.getSegment(i), locomotionParams)
  }

  private def updateParticleAccelerations(): Unit = {
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
  private def updateParticlePositions(timeStep: Double): Unit = {
    for (i <- 0 until snake.getNumSegments)
      segmentUpdater.updatePositions(snake.getSegment(i), timeStep)
  }
}