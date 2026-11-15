// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.placement.method

/** Inset rectangle used by uniform and Gaussian placement samplers. */
private[method] final case class PlacementBounds(margin: Int, width: Int, height: Int) {
  val innerWidth: Int = width - 2 * margin
  val innerHeight: Int = height - 2 * margin
}
