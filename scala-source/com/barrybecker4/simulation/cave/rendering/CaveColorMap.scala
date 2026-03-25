// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.rendering

import com.barrybecker4.math.Range
import com.barrybecker4.ui.util.ColorMap
import java.awt.Color

/**
  * Default colormap for cave visualization. May be edited in the UI.
  * @author Barry Becker
  */
object CaveColorMap {

  private val COLORS = Array(
    Color.WHITE,
    new Color(250, 215, 0),
    new Color(140, 230, 0),
    new Color(40, 150, 225),
    new Color(20, 0, 210),
    new Color(210, 101, 208)
  )

  /** For tests: evenly-spaced control points across the range. */
  private[simulation] def getControlPoints(range: Range): Array[Double] = {
    val floor = range.min
    val step = range.getExtent / (COLORS.length - 1)
    Array.tabulate(COLORS.length)(i => floor + i * step)
  }
}

class CaveColorMap(range: Range) extends ColorMap(CaveColorMap.getControlPoints(range), CaveColorMap.COLORS) {
  def this() = { this(Range(0, 1.0)) }
}
