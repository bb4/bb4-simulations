 // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.henonphase.algorithm

import java.awt._


/**
  * Henon travelers travel through time
  * @author Barry Becker
  */
class Traveler private[algorithm](var x: Double, var y: Double, val color: Color, var params: TravelerParams) {

  // last position
  private var lastX = x
  private var lastY = y

  private[algorithm] def getLastX = lastX
  private[algorithm] def getLastY = lastY

  /** increment forward one iteration */
  def increment(): Unit = {
    lastX = x
    lastY = y
    val sin = Math.sin(params.angle)
    val cos = Math.cos(params.angle)
    val term = params.multiplier * y + params.offset - x * x
    val temp = x * cos - term * sin
    y = x * sin + term * cos
    x = temp
  }
}
