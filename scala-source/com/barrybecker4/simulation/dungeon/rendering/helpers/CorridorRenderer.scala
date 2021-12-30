package com.barrybecker4.simulation.dungeon.rendering.helpers

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, Decoration}
import com.barrybecker4.ui.renderers.OfflineGraphics


// todo: add ability to draw polygon to offline renderer
case class CorridorRenderer(g: OfflineGraphics, options: DungeonOptions) {

  private val straightRenderer = StraightCorridorRenderer(g, options)

  def renderCorridors(corridors: Set[Corridor]): Unit = {
    val cellSize: Int = options.cellSize

    for (corridor <- corridors) {
      val path = corridor.paths
      straightRenderer.drawCorridor(path)
    }
  }
}
