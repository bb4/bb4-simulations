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
    val startTime = System.currentTimeMillis()
    val model = doGeneration(options, rnd)
    println(this.getClass.getSimpleName + ": "  + (System.currentTimeMillis() - startTime) / 1000.0)
    model
  }
  
  def doGeneration(options: DungeonOptions, rnd: Random): DungeonModel
}
