// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon

import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}

class DungeonGenerator {

  def generateDungeon(options: DungeonOptions):DungeonModel = DungeonModel(options)
}
