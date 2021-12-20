// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions, Room}
import com.barrybecker4.simulation.dungeon.rendering.DungeonRenderer.*
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color, Dimension, Graphics}


object DungeonRenderer {
  val STROKE = new BasicStroke(1.0)
  val GRID_COLOR = new Color(10,110, 180, 180)
  val GRID_STROKE = new BasicStroke(1.0)
}

class DungeonRenderer() {

  private var offlineGraphics: OfflineGraphics =
    new OfflineGraphics(new Dimension(10, 10), Color.WHITE)

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def render(dungeonOptions: DungeonOptions, dungeonModel: DungeonModel): Unit = synchronized {
    val graphics = getOfflineGraphics(dungeonOptions.getScreenDimension)
    graphics.clear()

    if (dungeonOptions.showGrid) {
      showGrid(graphics, dungeonOptions)
    }

    drawRooms(graphics, dungeonOptions, dungeonModel.rooms)
  }

  private def showGrid(g: OfflineGraphics, options: DungeonOptions): Unit = {
    val dim = options.dimension
    val scale = options.cellSize
    val width = dim.width * scale
    val height = dim.height * scale

    g.setColor(GRID_COLOR)
    g.setStroke(GRID_STROKE)

    for (j <- 0 to dim.height) { //  -----
      val ypos = (j * scale)
      g.drawLine(0, ypos, width, ypos)
    }
    for (i <- 0 to dim.width) { //  ||||
      val xpos = i * scale
      g.drawLine(xpos,
        0, xpos, height)
    }
  }

  private def drawRooms(g: OfflineGraphics, options: DungeonOptions, rooms: Set[Room]): Unit = {
    val cellSize: Int = options.cellSize
    val dim = options.dimension

    for (room <- rooms) {
      val scaledBox = room.box.scaleBy(cellSize)

      g.setColor(room.decoration.floorColor)
      g.fillBox(scaledBox)

      g.setColor(room.decoration.wallColor)
      g.setStroke(STROKE)
      g.drawBox(scaledBox)
    }
  }

  private def getOfflineGraphics(dim: Dimension): OfflineGraphics = {
    if (offlineGraphics.dim != dim) {
      offlineGraphics = new OfflineGraphics(dim, Color.WHITE)
    }
    offlineGraphics
  }

}
