package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.simulation.dungeon.generator.bsp.BspTree
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, Room}

import scala.collection.immutable.HashMap


case class CorridorGenerator(options: DungeonOptions) {

  def generateCorridors(bspTree: BspTree[Room]): Map[Room, Set[Corridor]] = {
    HashMap()
  }

}
