// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, RoomDecoration}
import com.barrybecker4.ui.renderers.OfflineGraphics


class CorridorRenderer {
  
  def renderCorridors(g: OfflineGraphics, options: DungeonOptions, corridors: Set[Corridor]): Unit = {
    val cellSize: Int = options.cellSize

    for (corridor <- corridors) {
      var previousLocation = corridor.path.head

      // todo: add ability to draw polygon to offline renderer
      for (location <- corridor.path.tail) {
        val px = previousLocation.getX
        val py = previousLocation.getY
        val x = location.getX
        val y = location.getY
        val box =
          if (Math.abs(x - px) > Math.abs(y - py))
            new Box(IntLocation(previousLocation.getY, previousLocation.getX),
                    IntLocation(location.getY + 1, location.getX))
          else
            new Box(IntLocation(previousLocation.getY, previousLocation.getX), 
                    IntLocation(location.getY, location.getX + 1))

        drawCorridorSegment(g, box.scaleBy(cellSize), corridor.decoration)
        previousLocation = location
      }
    }
  }

  private def drawCorridorSegment(g: OfflineGraphics, box: Box, decoration: RoomDecoration): Unit = {
    g.setColor(decoration.floorColor)
    g.fillBox(box)

    g.setColor(decoration.wallColor)
    g.setStroke(decoration.wallStroke)
    g.drawBox(box)
  }
}
