// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.generator.uniongraph.CorridorGenerator
import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.RoomGenerator
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions, Room}

import java.awt.{Color, Dimension}
import scala.collection.immutable.HashSet
import scala.collection.mutable
import scala.util.Random



/**
 * Place a random room, then divide the remainder into candidate regions that are placed on
 * the end of the queue. Pop next region off and repeat.
 */
class UnionGraphDungeonGenerator extends DungeonGeneratorStrategy {

  def generateDungeon(options: DungeonOptions): DungeonModel = {
    val rnd: Random = Random(0)
    val roomGenerator = RoomGenerator(options, rnd)
    val corridorGenerator = CorridorGenerator(options, rnd)

    val rooms = roomGenerator.generateRooms()
    val dungeonMap = corridorGenerator.generateCorridors(rooms)

    // Last param can be Some(dungeonMap)) if debugging
    DungeonModel(rooms, dungeonMap.getCorridors, None)
  }
}
