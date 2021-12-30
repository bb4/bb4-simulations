package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.simulation.dungeon.model.{DungeonMap, Room}


class RoomJoiner {

  /**
   * Send corridor rays out from that room and join with other nearby rooms or intersecting corridors.
   * Add the first corridor that joins with a room or corridor in another roomSet.
   * If none exist, just add the corridor to the nearest object. If nothing intersects
   */
  def doJoin(room: Room, dungeonMap: DungeonMap, roomsToRoomSet: RoomToRoomSetMap): RoomSet = {

    val allRooms = roomsToRoomSet.getAllRooms
    RoomSet(allRooms, roomsToRoomSet(allRooms.head).corridors, allRooms.head.box)
  }
}
