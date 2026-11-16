// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.gait

import com.barrybecker4.simulation.snake.LocomotionParameters

/**
  * Strategy for how a snake contracts muscles and scales friction for a locomotion gait.
  */
trait Gait {

  /**
    * @return (leftEdgeContraction, rightEdgeContraction) relative to resting length
    */
  def sideContractions(segmentIndex: Int, params: LocomotionParameters, time: Double): (Double, Double)

  /** Multiplier applied to frictional force on the segment center particle. */
  def frictionScale(segmentIndex: Int, params: LocomotionParameters, time: Double): Double = 1.0
}

object Gait {

  def wavePhase(segmentIndex: Int, params: LocomotionParameters, time: Double): Double =
    segmentIndex.toDouble / params.wavePeriod - params.waveSpeed * time

  def waveValue(segmentIndex: Int, params: LocomotionParameters, time: Double): Double =
    params.waveType.calculateOffset(wavePhase(segmentIndex, params, time))
}
