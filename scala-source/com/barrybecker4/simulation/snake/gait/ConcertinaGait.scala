// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.gait

import com.barrybecker4.simulation.snake.LocomotionParameters

/**
  * Accordion locomotion: a traveling fold window with higher grip while the rest stays near rest length.
  * Fold bending uses the same amplitude as other gaits (no boost) — a 2× boost made the model
  * spin and tear apart once wave amplitude was raised in the UI.
  */
object ConcertinaGait extends Gait {
  /** Fraction of the wave cycle that is actively folding. */
  private val FoldFraction = 0.30
  /** Soften fold bending relative to raw waveAmplitude. */
  private val FoldAmplitudeScale = 0.7
  private val FoldFriction = 1.6
  private val OutsideFriction = 0.9
  private val MinContraction = 0.15

  private def foldFraction(segmentIndex: Int, params: LocomotionParameters, time: Double): Double = {
    val theta = Gait.wavePhase(segmentIndex, params, time)
    val cycle = theta / (2.0 * Math.PI)
    var frac = cycle - Math.floor(cycle)
    if (frac < 0.0) frac += 1.0
    frac
  }

  private def inFoldWindow(segmentIndex: Int, params: LocomotionParameters, time: Double): Boolean =
    foldFraction(segmentIndex, params, time) < FoldFraction

  override def sideContractions(
      segmentIndex: Int,
      params: LocomotionParameters,
      time: Double
  ): (Double, Double) = {
    if (!inFoldWindow(segmentIndex, params, time)) (1.0, 1.0)
    else {
      val wave = Gait.waveValue(segmentIndex, params, time)
      val offset = FoldAmplitudeScale * params.waveAmplitude * (wave - params.direction)
      val left = math.max(MinContraction, 1.0 + offset)
      val right = math.max(MinContraction, 1.0 - offset)
      (left, right)
    }
  }

  override def frictionScale(
      segmentIndex: Int,
      params: LocomotionParameters,
      time: Double
  ): Double =
    if (inFoldWindow(segmentIndex, params, time)) FoldFriction else OutsideFriction
}
