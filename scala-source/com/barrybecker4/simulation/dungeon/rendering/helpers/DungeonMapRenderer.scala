// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering.helpers

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonMap, DungeonOptions, Room}
import com.barrybecker4.simulation.dungeon.rendering.helpers.DungeonMapRenderer.{CORRIDOR_COLOR, ROOM_COLOR}
import com.barrybecker4.simulation.dungeon.rendering.helpers.GridRenderer.{GRID_COLOR, GRID_STROKE}
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.{BasicStroke, Color}


object DungeonMapRenderer {
  private val ROOM_COLOR = new Color(255, 0, 10, 60)
  private val CORRIDOR_COLOR = new Color(240, 250, 0, 60)
}

/** Mostly used for debugging */
case class DungeonMapRenderer(g: OfflineGraphics, options: DungeonOptions) {

  private val scale = options.cellSize
  
  def render(dungeonMap: DungeonMap): Unit = {
    val dim = options.dimension

    for (j <- 0 to dim.height) {
      for (i <- 0 to dim.width) {
        drawCell(g, i, j, dungeonMap)
      }
    }
  }
  
  private def drawCell(g: OfflineGraphics, i: Int, j: Int, dungeonMap: DungeonMap): Unit = {
    val x = i * scale
    val y = j * scale
    dungeonMap(IntLocation(j, i)) match {
      case Some(room: Room) => {
        g.setColor(ROOM_COLOR)
        g.fillRect(x, y, scale, scale)
      }
      case Some(corridor: Corridor) => {
        g.setColor(CORRIDOR_COLOR)
        g.fillRect(x, y, scale, scale)
      }
      case None => {}
    }
  }
}
