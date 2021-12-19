// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.image.BufferedImage
import java.awt.{Color, Dimension, Graphics}


class DungeonRenderer() {

  private var offlineGraphics: OfflineGraphics = new OfflineGraphics(new Dimension(10, 10), Color.BLUE)

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def render(dungeonOptions: DungeonOptions, dungeonModel: DungeonModel): Unit = synchronized {
    val graphics = getOfflineGraphics(dungeonOptions.dimension)
    graphics.clear()
    val dim = dungeonOptions.dimension

    val cellSize: Int = dungeonOptions.cellSize
    for (room <- dungeonModel.rooms) {
      graphics.setColor(room.color)
      val xpos = room.location.getX * cellSize
      val ypos = room.location.getY * cellSize
      val width = room.dims.width * cellSize
      val height = room.dims.height * cellSize
      graphics.drawRect(xpos, ypos, width, height)
      graphics.fillRect(xpos + cellSize, ypos + cellSize, width - 2 * cellSize, height - 2 * cellSize)
    }
  }

  private def getOfflineGraphics(dim: Dimension): OfflineGraphics = {
    if (offlineGraphics.dim != dim) {
      offlineGraphics = new OfflineGraphics(dim, Color.LIGHT_GRAY)
    }
    offlineGraphics
  }

}
