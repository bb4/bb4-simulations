// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import java.awt.Dimension
import com.barrybecker4.simulation.dungeon.generator.bsp.BspTree


object DungeonModel {
  def getCorridors(rooms: Set[Room]): Set[Corridor] = rooms.flatMap(r => r.corridors)
}

/**
  * Represents the dungeon level as a graph.
  * It will have rooms - which are the nodes, and corridors - which are the edges connecting rooms.
  * The DungeonOptions determine how to generate the dungeon.
  * Once created, the dungeon can be rendered or operated on in other ways
  */
case class DungeonModel(bspTree: BspTree[Room]) {

  def getRooms: Set[Room] = bspTree.root.getChildren

}

