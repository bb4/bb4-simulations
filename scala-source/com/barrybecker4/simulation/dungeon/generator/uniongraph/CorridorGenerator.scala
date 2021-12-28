package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.simulation.dungeon.generator.bsp.room.RoomToCorridorsMap
import com.barrybecker4.simulation.dungeon.model.Room
import com.barrybecker4.simulation.dungeon.model.DungeonOptions


case class CorridorGenerator(options: DungeonOptions) {

  def generateCorridors(rooms: Set[Room]): RoomToCorridorsMap = {

    RoomToCorridorsMap()

  }
}
