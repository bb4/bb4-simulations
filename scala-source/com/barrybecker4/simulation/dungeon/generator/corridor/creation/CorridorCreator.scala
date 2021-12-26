package com.barrybecker4.simulation.dungeon.generator.corridor.creation

import com.barrybecker4.simulation.dungeon.generator.bsp.PartitionDirection
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room, RoomDecoration}

import java.awt.Color

object CorridorCreatorConsts {
  val MIN_OVERLAP: Int = 3
  val CORRIDOR_DECORATION: RoomDecoration = RoomDecoration(new Color(100, 0, 80), new Color(230, 190, 255))
}
