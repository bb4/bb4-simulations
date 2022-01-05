package com.barrybecker4.simulation.dungeon.generator.organic

import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.generator.organic.room.RoomGenerator
import com.barrybecker4.simulation.dungeon.model.DungeonModel
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions

import scala.util.Random


class OrganicDungeonGenerator extends DungeonGeneratorStrategy {

  def generateDungeon(options: DungeonOptions): DungeonModel = {
    val rnd: Random = Random(0)
    
    val roomGenerator = RoomGenerator(options, rnd)
    
    val dungeonMap = roomGenerator.generateRooms()

    // Last param can be Some(dungeonMap)) if debugging
    DungeonModel(dungeonMap.getRooms, dungeonMap.getCorridors, None)
  }
}
