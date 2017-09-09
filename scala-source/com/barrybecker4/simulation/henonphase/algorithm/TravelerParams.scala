// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase.algorithm

import TravelerParams._

/**
  * Henon traveler params are immutable.
  * @author Barry Becker
  */
object TravelerParams {
  val DEFAULT_PHASE_ANGLE = 4.995
  val DEFAULT_MULTIPLIER = 1.0
  val DEFAULT_OFFSET = 0.0
}

case class TravelerParams(angle: Double = DEFAULT_PHASE_ANGLE, multiplier: Double = DEFAULT_MULTIPLIER, offset: Double = DEFAULT_OFFSET) {

  def isDefaultMultiplier: Boolean = this.multiplier != TravelerParams.DEFAULT_MULTIPLIER
  def isDefaultOffset: Boolean = this.offset != TravelerParams.DEFAULT_OFFSET
}