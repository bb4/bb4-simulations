// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.rendering

import com.barrybecker4.simulation.dungeon.model.DungeonModel
import com.barrybecker4.ui.renderers.OfflineGraphics

import java.awt.image.BufferedImage
import java.awt.{Color, Dimension, Graphics}


class DungeonRenderer() {

  private var offlineGraphics: OfflineGraphics = new OfflineGraphics(new Dimension(100, 100), Color.BLUE)

  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get

  def render(dungeonModel: DungeonModel): Unit = synchronized {
    val graphics = getOfflineGraphics(dungeonModel.options.dimension)
    graphics.clear()
    graphics.setColor(Color.BLUE)
    val dim = dungeonModel.options.dimension
    println("w = " + dim.width + " w2=" + graphics.dim.width)
    graphics.drawRect(20, 20, dim.width - 40, dim.height - 40)
    graphics.setColor(Color.CYAN)
    graphics.fillRect(40, 40, dim.width - 80, dim.height - 80)
  }

  private def getOfflineGraphics(dim: Dimension): OfflineGraphics = {
    if (offlineGraphics.dim != dim) {
      offlineGraphics = new OfflineGraphics(dim, Color.LIGHT_GRAY)
    }
    offlineGraphics
  }

}
