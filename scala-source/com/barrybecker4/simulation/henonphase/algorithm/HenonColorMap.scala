// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase.algorithm

import com.barrybecker4.ui.util.ColorMap
import java.awt._


/**
  * Default colormap for visualization.
  * May be edited in the UI.
  * @author Barry Becker
  */
object HenonColorMap {
  private val MIN_VALUE = 0
  private val MAX_VALUE = 1.0
  private val RANGE = MAX_VALUE - MIN_VALUE
  private val ALPHA = 10
  private val VALUES = Array(
    MIN_VALUE,
    MIN_VALUE + 0.04 * RANGE,
    MIN_VALUE + 0.1 * RANGE,
    MIN_VALUE + 0.3 * RANGE,
    MIN_VALUE + 0.5 * RANGE,
    MIN_VALUE + 0.7 * RANGE,
    MIN_VALUE + 0.96 * RANGE,
    MIN_VALUE + RANGE
  )

  private val COLORS = Array(
    Color.WHITE,
    new Color(0, 0, 255, ALPHA), // .04
    new Color(100, 0, 250, ALPHA), // .1
    new Color(0, 255, 255, ALPHA), // .3
    new Color(0, 255, 0, ALPHA), // .5
    new Color(255, 255, 0, ALPHA), // .7
    new Color(255, 0, 0, ALPHA), // .96
    Color.BLACK)
}

class HenonColorMap private[algorithm](val alpha: Int) extends ColorMap(HenonColorMap.VALUES, HenonColorMap.COLORS) {
  this.setGlobalAlpha(alpha)
}
