// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon

import com.barrybecker4.simulation.dungeon.model.DungeonModel

import java.awt.{Color, Graphics}


class DungeonRenderer {

  def render(g: Graphics, dungeonModel: DungeonModel): Unit = {
    
    g.setColor(Color.BLUE)
    val dim = dungeonModel.options.dimension
    g.drawRect(20, 20, dim.width - 40, dim.height - 40)
  }
  
}
