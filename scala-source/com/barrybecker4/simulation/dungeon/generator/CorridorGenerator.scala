package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.simulation.dungeon.generator.bsp.{BspNode, BspTree, PartitionDirection}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, Room}

import scala.collection.immutable.HashMap



case class CorridorGenerator(options: DungeonOptions) {

  private var roomsToCorridors: Map[Room, Set[Corridor]] = _

  def generateCorridors(bspTree: BspTree[Room]): Map[Room, Set[Corridor]] = {
    roomsToCorridors = HashMap()
    addCorridorsToMap(bspTree.root)
    roomsToCorridors
  }
  
  private def addCorridorsToMap(node: BspNode[Room]): Unit = {
    if (node.data.isEmpty) {
      val partition1 = node.partition1.get
      val partition2 = node.partition2.get
      addCorridorsToMap(partition1)
      addCorridorsToMap(partition2)
      addCorridorsBetween(node.direction.get, partition1, partition2)
    }
  }
  
  private def addCorridorsBetween(direction: PartitionDirection, 
                                  node1: BspNode[Room], node2: BspNode[Room]): Unit = {
    if (direction == PartitionDirection.Horizontal) 
      addHorizontalConnections(node1: BspNode[Room], node2: BspNode[Room])
    else 
      addVerticalConnections(node1: BspNode[Room], node2: BspNode[Room])
  }
  
  private def addHorizontalConnections(leftNode: BspNode[Room], rightNode: BspNode[Room]): Unit = {
    // find all rightmost nodes in leftNode and leftMost in rightNode
    // add at leat one connection based on value of options.connectivity
  }

  private def addVerticalConnections(topNode: BspNode[Room], bottomNode: BspNode[Room]): Unit = {
  }
  
}
