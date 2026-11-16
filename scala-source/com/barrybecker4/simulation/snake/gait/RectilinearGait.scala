// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.gait

import com.barrybecker4.simulation.snake.LocomotionParameters

/**
  * Straight-body crawling: mostly equal bilateral contractions (no S-curve undulation)
  * with grip while shortening. A small left/right bias from [[LocomotionParameters.direction]]
  * steers without switching to serpentine motion.
  */
object RectilinearGait extends Gait {
  /** Fraction of waveAmplitude applied to the shared longitudinal contraction. */
  private[gait] val AmplitudeScale = 0.45
  /** How strongly direction biases L/R while keeping the body mostly straight. */
  private[gait] val SteerScale = 0.35
  private val ShorteningFriction = 1.35
  private val ElongatingFriction = 0.85
  private val MinContraction = 0.15

  override def sideContractions(
      segmentIndex: Int,
      params: LocomotionParameters,
      time: Double
  ): (Double, Double) = {
    val wave = Gait.waveValue(segmentIndex, params, time)
    val longitudinal = AmplitudeScale * params.waveAmplitude * wave
    // Same sign convention as undulatory: positive direction biases left longer / right shorter.
    val steer = SteerScale * params.waveAmplitude * params.direction
    val left = math.max(MinContraction, 1.0 + longitudinal - steer)
    val right = math.max(MinContraction, 1.0 + longitudinal + steer)
    (left, right)
  }

  override def frictionScale(
      segmentIndex: Int,
      params: LocomotionParameters,
      time: Double
  ): Double = {
    val wave = Gait.waveValue(segmentIndex, params, time)
    if (wave < 0.0) ShorteningFriction else ElongatingFriction
  }
}
