package com.barrybecker4.simulation.dungeon.generator.uniongraph.nodeset

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.Room


case class RoomSet(rooms: Set[Room], boundingBox: Box) {
  
  def mergeRoomSet(roomSet: RoomSet): RoomSet = {
    val otherBBox = roomSet.boundingBox
    val newBBox = boundingBox.expandBy(otherBBox.getTopLeftCorner).expandBy(otherBBox.getBottomRightCorner)
    RoomSet(this.rooms ++ roomSet.rooms, newBBox)
  }

}
