// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.rendering

import com.barrybecker4.common.math.Range
import com.barrybecker4.ui.util.ColorMap
import java.awt._

/**
  * Default colormap for cave visualization. May be edited in the UI.
  *
  * @author Barry Becker
  */
object ConwayColorMap {
  /** The color will not change beyond this timestep */
  private val MAX_TIME_STEP = 2000
  private val SHADOW_COLOR = new Color(120, 130, 180)
  private val COLORS = Array(
    SHADOW_COLOR,
    new Color(250, 180, 0),
    new Color(240, 230, 0),
    new Color(140, 230, 0),
    new Color(40, 150, 225),
    new Color(20, 0, 240),
    new Color(250, 10, 240),
    new Color(200, 0, 0),
    Color.BLACK
  )

  private def getControlPoints(range: Range) = {
    val floor = range.min
    val ceil = range.max + 0.000001 * range.getExtent
    val values = new Array[Double](COLORS.length)
    values(0) = floor
    val step = (range.getExtent - 1) / (COLORS.length - 2)
    var ct = 1
    for (v <- floor + 1 to ceil by step) {
      values(ct) = v
      ct += 1
    }
    values
  }
}

class ConwayColorMap()
  extends ColorMap(ConwayColorMap.getControlPoints(Range(1, ConwayColorMap.MAX_TIME_STEP)), ConwayColorMap.COLORS)
