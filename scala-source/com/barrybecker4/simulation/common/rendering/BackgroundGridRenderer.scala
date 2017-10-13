// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.rendering

import javax.vecmath.Vector2d
import java.awt._


/**
  * Use this for drawing the background grid.
  * @param gridColor the color for the grid.
  * @author Barry Becker
  */
class BackgroundGridRenderer(var gridColor: Color) {

  def drawGridBackground(g2: Graphics2D, cellSize: Double,
                         xDim: Int, yDim: Int, offset: Vector2d): Unit = {
    g2.setColor(gridColor)
    val xMax = (cellSize * xDim).toInt - 1
    val yMax = (cellSize * yDim).toInt - 1
    var pos = offset.y % cellSize
    for (j <- 0 to yDim) {
      val ht = (pos + j * cellSize).toInt
      g2.drawLine(1, ht, xMax, ht)
    }
    pos = offset.x % cellSize
    for (j <- 0 to xDim) {
      val w = (pos + j * cellSize).toInt
      g2.drawLine(w, 1, w, yMax)
    }
  }
}
