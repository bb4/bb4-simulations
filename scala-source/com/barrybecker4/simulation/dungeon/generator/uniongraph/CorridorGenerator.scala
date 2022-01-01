package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.{RoomJoiner, RoomSet, RoomToRoomSetMap}
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, DungeonOptions, Room}

import java.util.PriorityQueue
import scala.collection.mutable


case class CorridorGenerator(options: DungeonOptions) {

  private var roomToRoomSetMap: RoomToRoomSetMap = _
  private var dungeonMap: DungeonMap = _

  /**
   * Find corridors to connect a set of rooms.
   * The dungeonMap maps cells back to rooms and corridors
   */
  def generateCorridors(rooms: Set[Room]): DungeonMap = {

    roomToRoomSetMap = new RoomToRoomSetMap(rooms)
    dungeonMap = new DungeonMap(rooms)

    val roomJoiner = RoomJoiner(options.connectivity, options.dimension)

    var numDisjointSets = roomToRoomSetMap.getNumDisjointSets
    var sameCount = 0

    while (!roomToRoomSetMap.isConnected && sameCount < 4) {

      val roomSet = roomToRoomSetMap.getSmallestConnectedRoomSet
      
      joinRoomsInRooomSet(roomSet, roomJoiner)

      val numSets = roomToRoomSetMap.getNumDisjointSets
      if (numSets == numDisjointSets)
        sameCount += 1
      else
        sameCount = 0
      numDisjointSets = numSets
    }
    println("sameCount = " + sameCount + " isConnected = " + roomToRoomSetMap.isConnected)

    dungeonMap
  }

  private def joinRoomsInRooomSet(roomSet: RoomSet, roomJoiner: RoomJoiner): Unit = {
    
    println("num rooms in smallest roomSet = " + roomSet.rooms.size + " nCorridors = " + roomSet.corridors.size)

    for (room <- roomSet.rooms) {
      val result = roomJoiner.doJoin(room, dungeonMap, roomToRoomSetMap)
      dungeonMap = result._1
      roomToRoomSetMap = result._2
    }
  }
}
