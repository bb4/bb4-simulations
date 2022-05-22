// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.notes.math

import javax.vecmath.Vector2d

/**
  * distanceX = Vx t
  * where
  * t = 2 Vy / g
  * therefore
  * distanceX = 2 Vx Vy / g
  */
class DistanceCalculator() {

  def getDistance(velocityAtRelease: Vector2d): Double = {
    2.0 * velocityAtRelease.x * velocityAtRelease.y / AnalysisConstants.GRAVITY
  }
}
