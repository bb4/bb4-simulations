// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase.algorithm

/**
  * Henon traveler params are immutable.
  * @author Barry Becker
  */
object TravelerParams {
  val DEFAULT_PHASE_ANGLE = 4.995
  val DEFAULT_MULTIPLIER = 1.0
  val DEFAULT_OFFSET = 0.0
}

case class TravelerParams(
    angle: Double = TravelerParams.DEFAULT_PHASE_ANGLE,
    multiplier: Double = TravelerParams.DEFAULT_MULTIPLIER,
    offset: Double = TravelerParams.DEFAULT_OFFSET) {

  /** True when the multiplier differs from the default (show explicitly in the formula). */
  def usesExplicitMultiplier: Boolean = multiplier != TravelerParams.DEFAULT_MULTIPLIER

  /** True when the offset differs from the default (show explicitly in the formula). */
  def usesExplicitOffset: Boolean = offset != TravelerParams.DEFAULT_OFFSET
}