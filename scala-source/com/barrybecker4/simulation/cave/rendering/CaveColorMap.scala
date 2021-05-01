// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.rendering

import com.barrybecker4.math.Range
import com.barrybecker4.ui.util.ColorMap
import java.awt.Color
import scala.collection.immutable.Range.BigDecimal



/**
  * Default colormap for cave visualization. May be edited in the UI.
  * @author Barry Becker
  */
object CaveColorMap {

  private val COLORS = Array(
    Color.WHITE,
    new Color(250, 215, 0),
    new Color(140, 230, 0),
    //new Color(90, 210, 5),
    //new Color(0, 200, 230),
    new Color(40, 150, 225),
    //new Color(110, 60, 200),
    //new Color(200, 120, 90),
    new Color(20, 0, 210),
    new Color(210, 101, 208)
  )

  private def getControlPoints(range: Range) = {
    val floor = range.min
    val ceil = range.max + 0.000001 * range.getExtent
    val values = new Array[Double](COLORS.length)
    val step = range.getExtent / (COLORS.length - 1)
    var ct = 0
    for (v <- BigDecimal.inclusive(floor, ceil,step)) {
      values(ct) = v.toDouble
      ct += 1
    }
    values
  }
}

class CaveColorMap(range: Range) extends ColorMap(CaveColorMap.getControlPoints(range), CaveColorMap.COLORS) {
  def this() = { this(Range(0, 1.0)) }
}
