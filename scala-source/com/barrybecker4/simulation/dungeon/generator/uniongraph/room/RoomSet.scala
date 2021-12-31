package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room}

import scala.collection.immutable.HashSet


case class RoomSet(rooms: Set[Room], corridors: Set[Corridor], boundingBox: Box) {

  def this(room: Room) = {
    this(HashSet(room), Set(), room.box)
  }

  def mergeRoomSet(roomSet: RoomSet): RoomSet = {
    val otherBBox = roomSet.boundingBox
    val newBBox = boundingBox.expandBy(otherBBox.getTopLeftCorner).expandBy(otherBBox.getBottomRightCorner)
    RoomSet(this.rooms ++ roomSet.rooms, this.corridors ++ roomSet.corridors, newBBox)
  }
  
  def addCorridor(corridor: Corridor): RoomSet = RoomSet(rooms, corridors + corridor, boundingBox)
  
  def removeCorridor(corridor: Corridor): RoomSet = {
    assert(corridors.contains(corridor))
    RoomSet(rooms, corridors - corridor, boundingBox)
  }
  
  def size(): Int = rooms.size

}
