// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.gait

import com.barrybecker4.simulation.snake.LocomotionParameters

/**
  * Classic serpentine locomotion: opposite left/right traveling-wave contractions.
  */
object HorizontalUndulatoryGait extends Gait {

  override def sideContractions(
      segmentIndex: Int,
      params: LocomotionParameters,
      time: Double
  ): (Double, Double) = {
    val wave = Gait.waveValue(segmentIndex, params, time)
    val offset = params.waveAmplitude * (wave - params.direction)
    (1.0 + offset, 1.0 - offset)
  }
}
