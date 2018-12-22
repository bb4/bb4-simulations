// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.rendering


/**
  * Fluid rendering options
  * @author Barry Becker
  */
class RenderingOptions() {

  private var showVelocities = false

  def setShowVelocities(show: Boolean) { showVelocities = show}
  def getShowVelocities: Boolean = showVelocities
}
