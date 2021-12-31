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
    assert(dungeonMap != null)

    var numDisjointSets = roomToRoomSetMap.getNumDisjointSets
    var done = false
    var sameCount = 0

    while (!roomToRoomSetMap.isConnected && sameCount < 10) {
      val roomSet = roomToRoomSetMap.getSmallestConnectedRoomSet
      for (room <- roomSet.rooms) {
        val result = roomJoiner.doJoin(room, dungeonMap, roomToRoomSetMap)
        dungeonMap = result._1
        roomToRoomSetMap = result._2
        val numSets = roomToRoomSetMap.getNumDisjointSets
        if (numSets == numDisjointSets) {
          sameCount += 1
        } else {
          sameCount = 0
        }
        numDisjointSets = numSets
      }
    }
    println("sameCount = " + sameCount)

    dungeonMap
  }
}
