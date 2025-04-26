// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import com.barrybecker4.simulation.liquid.model.grid.Grid
import com.barrybecker4.simulation.liquid.model.LegacyParticle

trait LegacyEnvironment {
  def reset(): Unit
  def stepForward(timeStep: Double): Double

  def getWidth: Int
  def getHeight: Int
  def getGrid: Grid
  def getParticles: Iterator[LegacyParticle]

  def setViscosity(v: Double): Unit
  def setB0(b0: Double): Unit
}
