// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions, Room}

import scala.collection.immutable.HashSet
import java.awt.{Color, Dimension}


class DungeonGenerator {

  def generateDungeon(options: DungeonOptions):DungeonModel = {
    val rooms: Set[Room] = HashSet(
      Room(IntLocation(10, 20), Dimension(10, 12), Color.GREEN),
      Room(IntLocation(40, 21), Dimension(9, 7), Color.GREEN),
      Room(IntLocation(30, 31), Dimension(5, 10), Color.CYAN)
    )


    new DungeonModel(options.dimension, rooms)
  }
}
