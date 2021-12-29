package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.simulation.dungeon.generator.bsp.room.RoomToCorridorsMap
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, DungeonOptions, Room}


case class CorridorGenerator(options: DungeonOptions) {

  def generateCorridors(rooms: Set[Room]): DungeonMap = {

    val dungeonMap = DungeonMap(rooms)
    dungeonMap

  }
}
