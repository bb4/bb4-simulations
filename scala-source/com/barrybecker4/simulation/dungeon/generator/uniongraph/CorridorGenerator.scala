package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.{RoomJoiner, RoomSet, RoomToRoomSetMap}
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, DungeonOptions, Room}

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
    //var lastProcessedRoomSet: RoomSet = null

    while (!roomToRoomSetMap.isConnected && sameCount < 3) {

      val roomSet = roomToRoomSetMap.getSmallestConnectedRoomSet
      //assert(roomSet != lastProcessedRoomSet)
      //lastProcessedRoomSet = roomSet

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

    // println("num rooms in smallest roomSet = " + roomSet.rooms.size + " nCorridors = " + roomSet.corridors.size)

    for (room <- roomSet.rooms) {
      val result = roomJoiner.doJoin(room, dungeonMap, roomToRoomSetMap)
      dungeonMap = result._1
      roomToRoomSetMap = result._2
    }
  }
}
