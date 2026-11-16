// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.gait

import com.barrybecker4.simulation.snake.LocomotionParameters

/**
  * Lateral undulation with phase-modulated friction to approximate planted contact points.
  */
object SideWindingGait extends Gait {
  /** Gain for frictionScale = 1 + k * max(0, wave). */
  private val PlantGain = 1.5

  override def sideContractions(
      segmentIndex: Int,
      params: LocomotionParameters,
      time: Double
  ): (Double, Double) =
    HorizontalUndulatoryGait.sideContractions(segmentIndex, params, time)

  override def frictionScale(
      segmentIndex: Int,
      params: LocomotionParameters,
      time: Double
  ): Double = {
    val wave = Gait.waveValue(segmentIndex, params, time)
    1.0 + PlantGain * math.max(0.0, wave)
  }
}
