// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Room}
import com.barrybecker4.ui.renderers.OfflineGraphics

class RoomRenderer {

  def renderRooms(g: OfflineGraphics, options: DungeonOptions, rooms: Set[Room]): Unit = {
    val cellSize: Int = options.cellSize

    for (room <- rooms) {
      val scaledBox = room.box.scaleBy(cellSize)
      val decoration = room.decoration

      g.setColor(decoration.floorColor)
      g.fillBox(scaledBox)

      g.setColor(decoration.wallColor)
      g.setStroke(decoration.wallStroke)
      g.drawBox(scaledBox)
    }
  }

}
