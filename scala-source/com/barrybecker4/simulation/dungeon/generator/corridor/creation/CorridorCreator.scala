package com.barrybecker4.simulation.dungeon.generator.corridor.creation

import com.barrybecker4.simulation.dungeon.generator.bsp.PartitionDirection
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room}

/**
 * Interface for corridor creation strategies
 */
trait CorridorCreator {

  def createCorridorBetweenRooms(
                                  direction: PartitionDirection,
                                  room1: Room,
                                  room2: Room
                                ): Option[Corridor]

}
