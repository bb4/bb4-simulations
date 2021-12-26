package com.barrybecker4.simulation.dungeon.rendering.helpers

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, RoomDecoration}
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.Color

// todo: add ability to draw polygon to offline renderer
case class StraightCorridorRenderer(g: OfflineGraphics, options: DungeonOptions) {

  def drawCorridor(path: Seq[IntLocation], decoration: RoomDecoration): Unit = {
    val loc1 = path.head
    val loc2 = path(1)

    val px = loc1.getX
    val py = loc1.getY
    val x = loc2.getX
    val y = loc2.getY

    val box =
      if (Math.abs(x - px) > Math.abs(y - py))
        new Box(IntLocation(py, px), IntLocation(y + 1,x))
      else
        new Box(IntLocation(py, px), IntLocation(y, x + 1))

    drawCorridorSegment(box.scaleBy(options.cellSize), decoration)
  }

  private def drawCorridorSegment(box: Box, decoration: RoomDecoration): Unit = {
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

    if (isHorizontal) drawHorizontalLines(box)
    else drawVerticalLines(box)
  }

  private def drawHorizontalLines(box: Box): Unit = {
    g.drawLine(box.getMinCol, box.getMinRow, box.getMaxCol, box.getMinRow)
    g.drawLine(box.getMinCol, box.getMaxRow, box.getMaxCol, box.getMaxRow)
  }

  private def drawVerticalLines(box: Box): Unit = {
    g.drawLine(box.getMinCol, box.getMinRow, box.getMinCol, box.getMaxRow)
    g.drawLine(box.getMaxCol, box.getMinRow, box.getMaxCol, box.getMaxRow)
  }
}
