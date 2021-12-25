package com.barrybecker4.simulation.dungeon.generator.corridor.creation

import com.barrybecker4.common.geometry
import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.bsp.PartitionDirection
import com.barrybecker4.simulation.dungeon.generator.corridor.creation.CorridorCreator
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room}
import com.barrybecker4.simulation.dungeon.generator.corridor.creation.CorridorCreator.*


class AngledCorridorCreator extends CorridorCreator {

  /** this should always return a corridor */
  def createCorridorBetweenRooms(direction: PartitionDirection,
                                 room1: Room, room2: Room): Option[Corridor] = {

    val c = if (direction == PartitionDirection.Horizontal) acrossThenUpOrDown(room1, room2)
    else downThenLeftOrRight(room1, room2)

    Some(c)
  }

  private def acrossThenUpOrDown(room1: Room, room2: Room): Corridor = {
    val box1 = room1.box
    val box2 = room2.box
    val x1 = box1.getMaxCol
    val y1 = box1.getMinRow + box1.getHeight / 2
    val x2 = box2.getMinCol + box2.getWidth / 2
    val y2 = if (box1.getMaxRow < box2.getMinRow) box2.getMinRow else box2.getMaxRow

    createCorridor(x1, y1, x2, y2)
  }

  private def downThenLeftOrRight(room1: Room, room2: Room): Corridor = {
    val box1 = room1.box
    val box2 = room2.box
    val y1 = box1.getMaxRow
    val x1 = box1.getMinCol + box1.getWidth / 2
    val y2 = box2.getMinRow + box2.getHeight / 2
    val x2 = if (box1.getMaxCol < box2.getMinCol) box2.getMaxCol else box2.getMinCol

    createCorridor(x1, y1, x2, y2)
  }

  private def createCorridor(x1: Int, y1: Int, x2: Int, y2: Int): Corridor = {
    val path = Seq(
      IntLocation(y1, x1),
      IntLocation(y1, x2),
      IntLocation(y2, x2)
    )
    Corridor(path, CORRIDOR_DECORATION)
  }
}
