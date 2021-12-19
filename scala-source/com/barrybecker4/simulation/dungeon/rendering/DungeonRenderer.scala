// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.image.BufferedImage
import java.awt.{Color, Dimension, Graphics}


class DungeonRenderer() {

  private var offlineGraphics: OfflineGraphics = new OfflineGraphics(new Dimension(100, 100), Color.BLUE)

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def render(dungeonOptions: DungeonOptions, dungeonModel: DungeonModel): Unit = synchronized {
    val graphics = getOfflineGraphics(dungeonOptions.dimension)
    graphics.clear()
    val dim = dungeonOptions.dimension
    println("dim = " + dim.toString)

    val size: Int = dungeonOptions.cellSize
    for (room <- dungeonModel.rooms) {
      graphics.setColor(room.color)
      val xpos = room.location.getX * size
      val ypos = room.location.getY * size
      val width = room.dims.width * size
      val height = room.dims.height * size
      graphics.drawRect(xpos, ypos, width, height)
      val border = dungeonOptions.wallBorderWidth * size
      graphics.fillRect(xpos + border, ypos + border, width - 2 * border, height - 2 * border)
    }

  }

  private def getOfflineGraphics(dim: Dimension): OfflineGraphics = {
    if (offlineGraphics.dim != dim) {
      offlineGraphics = new OfflineGraphics(dim, Color.LIGHT_GRAY)
    }
    offlineGraphics
  }

}
