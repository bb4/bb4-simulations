// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.rendering

/**
  * Liquid Rendering options.
  * @author Barry Becker
  */
final class RenderingOptions() {

  private var showVelocities = true
  private var showPressures = true
  private var showCellStatus = true
  private var showGrid = true

  def setShowVelocities(show: Boolean): Unit = { showVelocities = show }
  def getShowVelocities: Boolean = showVelocities

  def setShowPressures(show: Boolean): Unit = { showPressures = show }
  def getShowPressures: Boolean = showPressures

  def setShowCellStatus(show: Boolean): Unit = {showCellStatus = show }
  def getShowCellStatus: Boolean = showCellStatus

  def setShowGrid(show: Boolean): Unit = {showGrid = show}
  def getShowGrid: Boolean = showGrid
}
