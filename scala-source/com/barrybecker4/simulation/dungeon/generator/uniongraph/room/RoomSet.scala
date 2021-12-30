package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.Room

import scala.collection.immutable.HashSet


case class RoomSet(rooms: Set[Room], boundingBox: Box) {

  def this(room: Room) = {
    this(HashSet(room), room.box)
  }

  def mergeRoomSet(roomSet: RoomSet): RoomSet = {
    val otherBBox = roomSet.boundingBox
    val newBBox = boundingBox.expandBy(otherBBox.getTopLeftCorner).expandBy(otherBBox.getBottomRightCorner)
    RoomSet(this.rooms ++ roomSet.rooms, newBBox)
  }

  def size(): Int = rooms.size

}
