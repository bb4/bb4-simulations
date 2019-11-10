/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import java.awt.Color
import com.barrybecker4.ui.util.ColorMap


/**
  * Default colormap for visualization. May be edited in the UI.
  * @author Barry Becker
  */
object MeshColorMap {
  private val MIN_VALUE = 0
  private val MAX_VALUE = 1.0
  private val RANGE = MAX_VALUE - MIN_VALUE

  private val VALUES = Array(
    MIN_VALUE, MIN_VALUE + 0.04 * RANGE,
    MIN_VALUE + 0.1 * RANGE,
    MIN_VALUE + 0.3 * RANGE,
    MIN_VALUE + 0.5 * RANGE,
    MIN_VALUE + 0.7 * RANGE,
    MIN_VALUE + 0.94 * RANGE,
    MIN_VALUE + RANGE)

  private val COLORS = Array(Color.WHITE, new Color(0, 0, 255), // .04
    new Color(100, 0, 250), // .1
    new Color(0, 255, 255), // .3
    new Color(0, 255, 0), // .5
    new Color(255, 255, 0), // .7
    new Color(255, 0, 0), // .94
    Color.BLACK)
}

class MeshColorMap() extends ColorMap(MeshColorMap.VALUES, MeshColorMap.COLORS)
