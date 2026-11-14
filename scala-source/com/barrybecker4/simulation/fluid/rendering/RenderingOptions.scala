// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.rendering


/**
  * Fluid rendering options
  */
object RenderingOptions {
  /** scales the size of everything. Number of pixels on edge of cell.   */
  private val DEFAULT_SCALE = 4
}

class RenderingOptions() {
  var isParallelized = false
  var scale: Double = RenderingOptions.DEFAULT_SCALE
  var showVelocities = false
  var showPressures = true
  var useLinearInterpolation = true
  var showGrid = false
}
