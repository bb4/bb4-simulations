package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.{RoomJoiner, RoomSet, RoomToRoomSetMap}
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, Room}

import java.util.PriorityQueue
import scala.collection.mutable
import scala.util.Random


object CorridorGenerator {
  private val RND = Random(0)
}

case class CorridorGenerator(options: DungeonOptions, rnd: Random = RND) {

  private var roomToRoomSetMap: RoomToRoomSetMap = _
  private var dungeonMap: DungeonMap = _

  /**
   * Find corridors to connect a set of rooms.
   * The dungeonMap maps cells back to rooms and corridors
   */
  def generateCorridors(rooms: Set[Room]): DungeonMap = {

    roomToRoomSetMap = new RoomToRoomSetMap(rooms)
    dungeonMap = new DungeonMap(rooms)

    val roomJoiner = RoomJoiner(options.connectivity, options.dimension, rnd)

    var numDisjointSets = roomToRoomSetMap.getNumDisjointSets
    var sameCount = 0

    while (!roomToRoomSetMap.isConnected && sameCount < 3) {

      val roomSet = roomToRoomSetMap.getSmallestConnectedRoomSet

      joinRoomsInRooomSet(roomSet, roomJoiner)

      val numSets = roomToRoomSetMap.getNumDisjointSets
      if (numSets == numDisjointSets)
        sameCount += 1
      else
        sameCount = 0
      numDisjointSets = numSets
    }
 
    dungeonMap
  }

  private def joinRoomsInRooomSet(roomSet: RoomSet, roomJoiner: RoomJoiner): Unit = {

    for (room <- roomSet.rooms) {
      val result = roomJoiner.doJoin(room, dungeonMap, roomToRoomSetMap)
      dungeonMap = result._1
      roomToRoomSetMap = result._2
    }
  }
}
