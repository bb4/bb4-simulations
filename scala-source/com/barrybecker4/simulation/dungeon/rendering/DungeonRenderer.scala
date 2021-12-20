// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}
import com.barrybecker4.simulation.dungeon.rendering.DungeonRenderer.STROKE
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color, Dimension, Graphics}


object DungeonRenderer {
  val STROKE = new BasicStroke(1.0)
}

class DungeonRenderer() {

  private var offlineGraphics: OfflineGraphics =
    new OfflineGraphics(new Dimension(10, 10), Color.BLUE)

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def render(dungeonOptions: DungeonOptions, dungeonModel: DungeonModel): Unit = synchronized {
    val graphics = getOfflineGraphics(dungeonOptions.getScreenDimension)
    graphics.clear()
    val dim = dungeonOptions.dimension

    val cellSize: Int = dungeonOptions.cellSize
    for (room <- dungeonModel.rooms) {
      graphics.setColor(Color.WHITE)
      val point = room.box.getTopLeftCorner
      val xpos = point.getX * cellSize
      val ypos = point.getY * cellSize
      val width = room.box.getWidth * cellSize
      val height = room.box.getHeight * cellSize

      graphics.fillRect(xpos, ypos, width, height)
      graphics.setColor(room.decoration.wallColor)
      graphics.setStroke(STROKE)

      graphics.drawRect(xpos, ypos, width, height)
      graphics.setColor(room.decoration.floorColor)
      graphics.fillRect(xpos + cellSize/2, ypos + cellSize/2, width - cellSize, height - cellSize)
    }
  }

  private def getOfflineGraphics(dim: Dimension): OfflineGraphics = {
    if (offlineGraphics.dim != dim) {
      offlineGraphics = new OfflineGraphics(dim, Color.LIGHT_GRAY)
    }
    offlineGraphics
  }

}
