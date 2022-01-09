package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room}

import scala.collection.immutable.HashSet


case class RoomSet(rooms: Set[Room], corridors: Set[Corridor]) {

  def this(room: Room) = {
    this(HashSet(room), Set())
  }

  def mergeRoomSet(roomSet: RoomSet): RoomSet = 
    RoomSet(this.rooms ++ roomSet.rooms, this.corridors ++ roomSet.corridors)

  // assuming that the corridor added does not connect any new rooms
  def addCorridor(corridor: Corridor): RoomSet = RoomSet(rooms, corridors + corridor)

  def removeCorridor(corridor: Corridor): RoomSet = {
    assert(corridors.contains(corridor), "Did not find " + corridor + " among \n" + corridors)
    RoomSet(rooms, corridors - corridor)
  }

  def size(): Int = rooms.size
}
