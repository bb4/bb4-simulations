package com.barrybecker4.simulation.dungeon.generator.corridor.creation

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.bsp.PartitionDirection
import com.barrybecker4.simulation.dungeon.generator.corridor.creation.CorridorCreator
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room}


class AngledCorridorCreator extends CorridorCreator {

  def createCorridorBetweenRooms(direction: PartitionDirection,
                                 room1: Room, room2: Room): Option[Corridor] = {
                                 
    None
    /*
    val overlap: Option[Box] = findOverlap(room1, room2, MIN_OVERLAP)
    if (overlap.isEmpty) None
    else Some(findCorridorForOverlap(overlap.get, direction, room1, room2))
     */
  }
}
