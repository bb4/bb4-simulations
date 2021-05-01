// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.rendering

import com.barrybecker4.common.concurrency._


/**
  * Fluid rendering options
  * @author Barry Becker
  */
object RenderingOptions {
  /** scales the size of everything. Number of pixels on edge of cell.   */
  private val DEFAULT_SCALE = 4
}

class RenderingOptions() {

  var isParallelized = true

  private var scale: Double = RenderingOptions.DEFAULT_SCALE
  private var showVelocities = false
  private var showPressures = true
  private var useLinearInterpolation = true
  private var showGrid = false

  def setScale(scale: Double): Unit = {this.scale = scale }
  def getScale: Double = scale

  def setShowVelocities(show: Boolean): Unit = { showVelocities = show}
  def getShowVelocities: Boolean = showVelocities

  def setShowPressures(show: Boolean): Unit = { showPressures = show }
  def getShowPressures: Boolean = showPressures

  def setUseLinearInterpolation(useInterp: Boolean): Unit = { useLinearInterpolation = useInterp}
  def getUseLinearInterpolation: Boolean = useLinearInterpolation

  def getShowGrid: Boolean = showGrid
  def setShowGrid(showGrid: Boolean): Unit = { this.showGrid = showGrid }
}
