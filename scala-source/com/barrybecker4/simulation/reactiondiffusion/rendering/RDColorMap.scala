/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

import com.barrybecker4.ui.util.ColorMap
import java.awt._


/**
  * Default colormap for visualization. May be edited in the UI.
  * @author Barry Becker
  */
object RDColorMap {
  private val MIN_CONCENTRATION = 0
  private val MAX_CONCENTRATION = 1.0
  private val RANGE = MAX_CONCENTRATION - MIN_CONCENTRATION
  private val VALUES = Array(
    MIN_CONCENTRATION,
    MIN_CONCENTRATION + 0.04 * RANGE,
    MIN_CONCENTRATION + 0.1 * RANGE,
    MIN_CONCENTRATION + 0.3 * RANGE,
    MIN_CONCENTRATION + 0.5 * RANGE,
    MIN_CONCENTRATION + 0.7 * RANGE,
    MIN_CONCENTRATION + 0.94 * RANGE,
    MIN_CONCENTRATION + RANGE)
  private val COLORS = Array(Color.BLACK, new Color(0, 0, 255), // .04
    new Color(100, 0, 250), // .1
    new Color(0, 255, 255), // .3
    new Color(0, 255, 0), // .5
    new Color(255, 255, 0), // .7
    new Color(255, 0, 0), // .94
    Color.BLACK)
}

class RDColorMap() extends ColorMap(RDColorMap.VALUES, RDColorMap.COLORS)
