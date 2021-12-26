package com.barrybecker4.simulation.dungeon.generator.corridor.creation

import com.barrybecker4.common.geometry
import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.bsp.PartitionDirection
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room}
import com.barrybecker4.simulation.dungeon.generator.corridor.creation.CorridorCreatorConsts.*


class AngledCorridorCreator {

  /** this should always return a corridor */
  def createCorridorBetweenRooms(direction: PartitionDirection,
                                 room1: Room, room1HasFreeSpace: Boolean,
                                 room2: Room, room2HasFreeSpace: Boolean): Option[Corridor] = {

    if (direction == PartitionDirection.Horizontal) {
      if (room1HasFreeSpace) Some(verticalThenHorizontal(room1, room2))
      else if (room2HasFreeSpace) Some(horizontalThenVertical(room1, room2))
      else None
    }
    else {
      if (room1HasFreeSpace) Some(horizontalThenVertical(room1, room2))
      else if (room2HasFreeSpace) Some(verticalThenHorizontal(room1, room2))
      else None
    }
  }

  private def horizontalThenVertical(room1: Room, room2: Room): Corridor = {
    val box1 = room1.box
    val box2 = room2.box
    val x1 = if (box1.getMaxCol < box2.getMinCol + MIN_OVERLAP) box1.getMaxCol else box1.getMinCol
    val y1 = box1.getMinRow + box1.getHeight / 2
    val x2 = box2.getMinCol + box2.getWidth / 2
    val y2 = if (box1.getMaxRow < box2.getMinRow + MIN_OVERLAP) box2.getMinRow else box2.getMaxRow

    val path = Seq(
      IntLocation(y1, x1),
      IntLocation(y1, x2),
      IntLocation(y2, x2)
    )
    Corridor(path, CORRIDOR_DECORATION)
  }

  private def verticalThenHorizontal(room1: Room, room2: Room): Corridor = {
    val box1 = room1.box
    val box2 = room2.box
    val x1 = box1.getMinCol + box1.getWidth / 2
    val y1 = if (box1.getMaxRow < box2.getMinRow + MIN_OVERLAP) box1.getMaxRow else box1.getMinRow
    val x2 = if (box1.getMaxCol < box2.getMinCol + MIN_OVERLAP) box2.getMinCol else box2.getMaxCol
    val y2 = box2.getMinRow + box2.getHeight / 2

    val path = Seq(
      IntLocation(y1, x1),
      IntLocation(y2, x1),
      IntLocation(y2, x2)
    )
    Corridor(path, CORRIDOR_DECORATION)
  }
}
