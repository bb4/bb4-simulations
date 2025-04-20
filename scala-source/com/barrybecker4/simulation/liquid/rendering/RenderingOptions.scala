// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.rendering

/**
  * Liquid Rendering options.
  * @author Barry Becker
  */
final class RenderingOptions() {
  
  private var showGrid = true
  
  def setShowGrid(show: Boolean): Unit = {showGrid = show}
  def getShowGrid: Boolean = showGrid
}
