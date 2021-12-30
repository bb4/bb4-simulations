package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.simulation.dungeon.model.Room


case class RoomToRoomSetMap(map: Map[Room, RoomSet]) {

  def this(rooms: Set[Room]) = {
    this(rooms.map(room => room -> new RoomSet(room)).toMap)
  }

  def update(roomSet: RoomSet): RoomToRoomSetMap = {
    var m: Map[Room, RoomSet] = map
    for (room <- roomSet.rooms) {
      m = m + (room -> roomSet)
    }
    RoomToRoomSetMap(map)
  }

  def getAllRooms: Set[Room] = map.keySet

  def isConnected: Boolean = map.head._2.size() == map.size

  def getSmallestConnectedRoomSet: RoomSet = map.values.minBy(_.size())
}
