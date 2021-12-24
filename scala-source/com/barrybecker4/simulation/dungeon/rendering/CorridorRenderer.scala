// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, RoomDecoration}
import com.barrybecker4.ui.renderers.OfflineGraphics
import java.awt.Color


case class CorridorRenderer(roomFloorColor: Color) {

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

    val isHorizontal = box.getWidth > box.getHeight
    val wallWidth = decoration.wallStroke.getLineWidth.toInt
    g.setColor(decoration.floorColor)

    if (isHorizontal)
      g.fillRect(box.getMinCol - wallWidth, box.getMinRow, box.getWidth + 2 * wallWidth, box.getHeight)
    else
      g.fillRect(box.getMinCol, box.getMinRow - wallWidth, box.getWidth, box.getHeight + 2 * wallWidth)

    g.setColor(decoration.wallColor)
    g.setStroke(decoration.wallStroke)

    if (isHorizontal) drawHorizontalLines(g, box)
    else drawVerticalLines(g, box)
  }

  private def drawHorizontalLines(g: OfflineGraphics, box: Box): Unit = {
    g.drawLine(box.getMinCol, box.getMinRow, box.getMaxCol, box.getMinRow)
    g.drawLine(box.getMinCol, box.getMaxRow, box.getMaxCol, box.getMaxRow)
  }

  private def drawVerticalLines(g: OfflineGraphics, box: Box): Unit = {
    g.drawLine(box.getMinCol, box.getMinRow, box.getMinCol, box.getMaxRow)
    g.drawLine(box.getMaxCol, box.getMinRow, box.getMaxCol, box.getMaxRow)
  }
}
