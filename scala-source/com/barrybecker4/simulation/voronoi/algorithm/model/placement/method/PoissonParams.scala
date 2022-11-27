// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.placement.method

import com.barrybecker4.simulation.voronoi.algorithm.model.placement.method.PoissonParams.*

/**
  * Henon traveler params are immutable.
  * @author Barry Becker
  */
object PoissonParams {
  val DEFAULT_RADIUS = 30.0
  val DEFAULT_K = 10
}

case class PoissonParams(radius: Double = DEFAULT_RADIUS, k: Int = DEFAULT_K) 