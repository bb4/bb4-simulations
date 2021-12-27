// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.generator.uniongraph.CorridorGenerator
import com.barrybecker4.simulation.dungeon.generator.uniongraph.RoomGenerator
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions, Room}

import java.awt.{Color, Dimension}
import scala.collection.immutable.HashSet


/**
 * place a random room, then divide the remainder into candidate regions that are placed on
 * the end of the queue. Pop next regipon off and repeat.
 */
class RandomPartitionDungeonGenerator extends DungeonGeneratorStrategy {

  def generateDungeon(options: DungeonOptions): DungeonModel = {
    val roomGenerator = RoomGenerator(options)
    val corridorGenerator = CorridorGenerator(options)

    val rooms = roomGenerator.generateRooms()
    val roomsToCorridors = corridorGenerator.generateCorridors(rooms)

    DungeonModel(rooms, roomsToCorridors)
  }
}
