// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonModel, DungeonOptions, Room, RoomDecoration}
import com.barrybecker4.simulation.dungeon.rendering.DungeonRenderer.*
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color, Dimension, Graphics}


object DungeonRenderer {
  val GRID_COLOR = new Color(10, 110, 180, 80)
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

    val rooms = dungeonModel.getRooms
    drawRooms(graphics, dungeonOptions, rooms)
    drawCorridors(graphics, dungeonOptions, DungeonModel.getCorridors(rooms))
  }

  private def showGrid(g: OfflineGraphics, options: DungeonOptions): Unit = {
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

  private def drawRooms(g: OfflineGraphics, options: DungeonOptions, rooms: Set[Room]): Unit = {
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

  private def drawCorridors(g: OfflineGraphics, options: DungeonOptions, corridors: Set[Corridor]): Unit = {
    val cellSize: Int = options.cellSize

    for (corridor <- corridors) {
      var previousLocation = corridor.path.head

      // todo: add ability to draw polygon to offline renderer
      for (location <- corridor.path.tail) {
        val box = new Box(IntLocation(previousLocation.getY, previousLocation.getX), IntLocation(location.getY + 1, location.getX + 1))
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

  private def getOfflineGraphics(dim: Dimension): OfflineGraphics = {
    if (offlineGraphics.dim != dim) {
      offlineGraphics = new OfflineGraphics(dim, Color.WHITE)
    }
    offlineGraphics
  }

}
