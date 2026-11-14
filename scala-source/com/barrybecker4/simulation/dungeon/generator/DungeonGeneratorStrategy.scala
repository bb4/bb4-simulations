// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.simulation.dungeon.generator.uniongraph.CorridorGenerator
import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.RoomGenerator
import com.barrybecker4.simulation.dungeon.model.DungeonModel
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions
import scala.util.Random


trait DungeonGeneratorStrategy {
  
  
  def generateDungeon(options: DungeonOptions): DungeonModel = {
    val rnd = new Random(0)
    doGeneration(options, rnd)
  }
  
  def doGeneration(options: DungeonOptions, rnd: Random): DungeonModel
}
