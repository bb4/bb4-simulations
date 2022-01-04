// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering.helpers

import com.barrybecker4.simulation.dungeon.model.Room
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions.DECORATION
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions


case class RoomRenderer(g: OfflineGraphics, options: DungeonOptions) {

  def renderRooms(rooms: Set[Room]): Unit = {
    val cellSize: Int = options.cellSize

    for (room <- rooms) {
      val scaledBox = room.box.scaleBy(cellSize)
   
      g.setColor(DECORATION.floorColor)
      g.fillBox(scaledBox)

      g.setColor(DECORATION.wallColor)
      g.setStroke(DECORATION.wallStroke)
      g.drawBox(scaledBox)
    }
  }

}
