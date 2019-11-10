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
    MIN_VALUE,
    MIN_VALUE + 0.2 * RANGE,
    MIN_VALUE + 0.4 * RANGE,
    MIN_VALUE + 0.6 * RANGE,
    MIN_VALUE + 0.8 * RANGE,
    MIN_VALUE + RANGE)

  private val COLORS = Array(
    new Color(255, 0, 0),  // 0
    new Color(250, 0, 255), // .2
    new Color(0, 0, 255), // .4
    new Color(0, 255, 0), // .8
    new Color(255, 255, 0), // .7
    new Color(255, 0, 0)
  )
}

class MeshColorMap() extends ColorMap(MeshColorMap.VALUES, MeshColorMap.COLORS)
