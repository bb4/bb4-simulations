// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.simulation.dungeon.model.DungeonOptions
import com.barrybecker4.simulation.dungeon.rendering.GridRenderer.{GRID_COLOR, GRID_STROKE}
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.{BasicStroke, Color}


object GridRenderer {
  private val GRID_COLOR = new Color(10, 110, 180, 80)
  private val GRID_STROKE = new BasicStroke(1.0)
}

class GridRenderer {

  def renderGrid(g: OfflineGraphics, options: DungeonOptions): Unit = {
    val dim = options.dimension
    val scale = options.cellSize
    val width = dim.width * scale
    val height = dim.height * scale

    g.setColor(GRID_COLOR)
    g.setStroke(GRID_STROKE)

    for (j <- 0 to dim.height) { //  -----
      val ypos = j * scale
      g.drawLine(0, ypos, width, ypos)
    }
    for (i <- 0 to dim.width) { //  ||||
      val xpos = i * scale
      g.drawLine(xpos,
        0, xpos, height)
    }
  }
}
