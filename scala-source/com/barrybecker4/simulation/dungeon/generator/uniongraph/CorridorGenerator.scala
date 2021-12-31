package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.{RoomJoiner, RoomSet, RoomToRoomSetMap}
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, DungeonOptions, Room}

import java.util.PriorityQueue
import scala.collection.mutable


case class CorridorGenerator(options: DungeonOptions) {

  /**
   * Find corridors to connect a set of rooms.
   * The dungeonMap maps cells back to rooms and corridors
   */
  def generateCorridors(rooms: Set[Room]): DungeonMap = {

    var roomToRoomSetMap: RoomToRoomSetMap = new RoomToRoomSetMap(rooms)
    var dungeonMap = new DungeonMap(rooms)
    val roomJoiner = RoomJoiner(options.connectivity, options.dimension)

/*
    while (!roomToRoomSetMap.isConnected) {
      val roomSet = roomToRoomSetMap.getSmallestConnectedRoomSet
      for (room <- roomSet.rooms) {
        val result = roomJoiner.doJoin(room, dungeonMap, roomToRoomSetMap)
        dungeonMap = result._1
        roomToRoomSetMap = result._2
      }
    }*/

    dungeonMap
  }

}
