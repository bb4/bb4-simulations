package com.barrybecker4.simulation.dungeon.rendering.helpers

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, Decoration}
import com.barrybecker4.simulation.dungeon.model.DungeonOptions.DECORATION
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.Color

// todo: add ability to draw polygon to offline renderer
case class StraightCorridorRenderer(g: OfflineGraphics, options: DungeonOptions) {

  def drawCorridor(paths: Seq[(IntLocation, IntLocation)]): Unit = {

    for (segment <- paths) {
      val loc1 = segment._1
      val loc2 = segment._2

      val px = loc1.getX
      val py = loc1.getY
      val x = loc2.getX
      val y = loc2.getY

      val isHorizontal = Math.abs(x - px) > Math.abs(y - py)
      val point2 = if (isHorizontal) IntLocation(y + 1, x) else IntLocation(y, x + 1)
      val box = new Box(IntLocation(py, px), point2)

      drawCorridorSegment(box.scaleBy(options.cellSize), DECORATION)
    }
  }

  private def drawCorridorSegment(box: Box, decoration: Decoration): Unit = {
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
