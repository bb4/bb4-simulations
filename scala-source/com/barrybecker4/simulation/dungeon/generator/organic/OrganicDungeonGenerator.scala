// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.organic

import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.generator.organic.room.RoomGenerator
import com.barrybecker4.simulation.dungeon.model.DungeonModel
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions

import scala.util.Random


class OrganicDungeonGenerator extends DungeonGeneratorStrategy {

  override def doGeneration(options: DungeonOptions, rnd: Random): DungeonModel = {
    
    val roomGenerator = RoomGenerator(options, rnd)
    val dungeonMap = roomGenerator.generateRooms()

    // Last param can be Some(dungeonMap)) if debugging
    DungeonModel(dungeonMap.getRooms, dungeonMap.getCorridors, None)
  }
}
