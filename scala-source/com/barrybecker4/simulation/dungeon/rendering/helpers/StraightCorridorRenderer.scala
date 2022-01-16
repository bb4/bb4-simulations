package com.barrybecker4.simulation.dungeon.rendering.helpers

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, Decoration, Orientation, Path}
import com.barrybecker4.simulation.dungeon.model.Orientation.*
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions.DECORATION
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.Color


case class StraightCorridorRenderer(g: OfflineGraphics, options: DungeonOptions) {

  def drawCorridor(corridor: Corridor): Unit = {

    val paths: Seq[Path] = corridor.paths

    for (path <- paths) {
      val px = path.start.getX
      val py = path.start.getY

      val point2 =
        path.orientation match {
          case Horizontal => IntLocation(py + 1, px + path.length)
          case Vertical => IntLocation(py + path.length, px + 1)
        }
      val box = new Box(path.start, point2)

      drawCorridorSegment(box.scaleBy(options.cellSize), path.orientation, DECORATION)
    }
  }

  private def drawCorridorSegment(box: Box, orientation: Orientation, decoration: Decoration): Unit = {
    g.setColor(decoration.floorColor)

    val wallWidth = decoration.wallStroke.getLineWidth.toInt
    g.setColor(decoration.floorColor)

    if (orientation == Horizontal)
      g.fillRect(box.getMinCol - wallWidth, box.getMinRow, box.getWidth + 2 * wallWidth, box.getHeight)
    else
      g.fillRect(box.getMinCol, box.getMinRow - wallWidth, box.getWidth, box.getHeight + 2 * wallWidth)

    g.setColor(decoration.wallColor)
    g.setStroke(decoration.wallStroke)

    if (orientation == Horizontal) drawHorizontalLines(box)
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
