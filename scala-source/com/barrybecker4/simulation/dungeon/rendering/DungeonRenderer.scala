// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonModel, DungeonOptions, Room, RoomDecoration}
import com.barrybecker4.simulation.dungeon.rendering.DungeonRenderer.BACKGROUND_COLOR
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color, Dimension, Graphics}


object DungeonRenderer {
  private val BACKGROUND_COLOR = Color.WHITE
}

class DungeonRenderer {

  private val roomRenderer = RoomRenderer()
  private val corridorRenderer = CorridorRenderer(BACKGROUND_COLOR)
  private val gridRenderer = GridRenderer()

  private var offlineGraphics: OfflineGraphics =
    new OfflineGraphics(new Dimension(10, 10), BACKGROUND_COLOR)

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def render(dungeonOptions: DungeonOptions, dungeonModel: DungeonModel): Unit = synchronized {
    val graphics = getOfflineGraphics(dungeonOptions.getScreenDimension)
    graphics.clear()

    if (dungeonOptions.showGrid) {
      gridRenderer.renderGrid(graphics, dungeonOptions)
    }

    roomRenderer.renderRooms(graphics, dungeonOptions, dungeonModel.getRooms)
    corridorRenderer.renderCorridors(graphics, dungeonOptions, dungeonModel.getCorridors)
  }

  private def getOfflineGraphics(dim: Dimension): OfflineGraphics = {
    if (offlineGraphics.dim != dim) {
      offlineGraphics = new OfflineGraphics(dim, BACKGROUND_COLOR)
    }
    offlineGraphics
  }

}
