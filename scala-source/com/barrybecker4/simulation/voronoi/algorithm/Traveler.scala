 // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.voronoi.algorithm

import java.awt.*


/**
  * Henon travelers travel through time
  * @author Barry Becker
  */
class Traveler private[algorithm](var x: Double, var y: Double, val color: Color, var params: PoissonParams) {

  // last position
  private var lastX = x
  private var lastY = y

  private[algorithm] def getLastX = lastX
  private[algorithm] def getLastY = lastY

  /** increment forward one iteration */
  def increment(): Unit = {
    lastX = x
    lastY = y
    val sin = Math.sin(params.radius)
    val cos = Math.cos(params.radius)
    val temp = x * cos - sin
    y = x * sin + cos
    x = temp
  }
}
