package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.simulation.dungeon.generator.uniongraph.nodeset.RoomSet
import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.RoomJoiner
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, DungeonOptions, Room}

import java.util.PriorityQueue
import scala.collection.mutable


case class CorridorGenerator(options: DungeonOptions) {

  /**
   * Find corridors to connect a set of rooms.
   * The dungeonMap maps cells back to rooms and corridors
   */
  def generateCorridors(rooms: Set[Room]): DungeonMap = {

    var roomToRoomSet: Map[Room, RoomSet] = rooms.map(room => room -> new RoomSet(room)).toMap
    var dungeonMap = DungeonMap(rooms)
    val roomJoiner = RoomJoiner()

    val numRooms = rooms.size

/*
    while (roomToRoomSet.head._2.size() < numRooms) {
      val roomSet = roomToRoomSet.values.minBy(_.size())

      for (room <- roomSet.rooms) {
        val newRoomSet = roomJoiner.doJoin(room, dungeonMap, roomToRoomSet)
        roomToRoomSet = update(newRoomSet, roomToRoomSet)
      }

    }*/
    dungeonMap
  }

  private def update(roomSet: RoomSet, roomToRoomSet: Map[Room, RoomSet]): Map[Room, RoomSet] = {
    var updatedRoomToRoomSet = roomToRoomSet
    for (room <- roomSet.rooms) {
      updatedRoomToRoomSet = updatedRoomToRoomSet + (room -> roomSet)
    }
    updatedRoomToRoomSet
  }
}
