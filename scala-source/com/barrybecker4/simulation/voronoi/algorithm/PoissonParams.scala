// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm

import com.barrybecker4.simulation.voronoi.algorithm.PoissonParams.*

/**
  * Henon traveler params are immutable.
  * @author Barry Becker
  */
object PoissonParams {
  val DEFAULT_RADIUS = 4.995
  val DEFAULT_K = 10
}

case class PoissonParams(radius: Double = DEFAULT_RADIUS, k: Double = DEFAULT_K) {

  def isDefaultK: Boolean = this.k != PoissonParams.DEFAULT_K
}