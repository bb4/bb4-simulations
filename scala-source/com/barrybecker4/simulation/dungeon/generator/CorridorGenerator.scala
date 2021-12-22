package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.bsp.{BspNode, BspTree, PartitionDirection}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, Room}

import scala.collection.immutable.HashMap



case class CorridorGenerator(options: DungeonOptions) {

  private var roomsToCorridors: Map[Room, Set[Corridor]] = _

  def generateCorridors(bspTree: BspTree[Room]): Map[Room, Set[Corridor]] = {
    roomsToCorridors = HashMap()
    addCorridorsToMap(bspTree.root)
    // consider adding corridors for all nearby rooms if connectivity above some threshold
    roomsToCorridors
  }

  private def addCorridorsToMap(node: BspNode[Room]): Unit = {
    if (node.data.isEmpty) {
      val partition1 = node.partition1.get
      val partition2 = node.partition2.get
      addCorridorsToMap(partition1)
      addCorridorsToMap(partition2)
      addCorridorsBetween(node.direction.get, node.splitPosition.get, partition1, partition2)
    }
  }

  private def addCorridorsBetween(direction: PartitionDirection, splitPos: Int,
                                  node1: BspNode[Room], node2: BspNode[Room]): Unit = {
    if (direction == PartitionDirection.Horizontal)
      addHorizontalConnections(splitPos, node1, node2)
    else
      addVerticalConnections(splitPos, node1, node2)
  }

  private def addHorizontalConnections(splitPos: Int, leftNode: BspNode[Room], rightNode: BspNode[Room]): Unit = {
    // find all rightmost nodes in leftNode and leftMost in rightNode
    // add at let one connection based on value of options.connectivity
    // might need to broaden the range searched for until wer find at least one room
    val rightEdgeBox = Box(0, splitPos - options.getMinPaddedDim, options.dimension.height, splitPos)
    val leftRooms = findRoomsGivenBoxFilter(rightEdgeBox, leftNode)
    val leftEdgeBox = Box(0, splitPos, options.dimension.height, splitPos + - options.getMinPaddedDim)
    val rightRooms = findRoomsGivenBoxFilter( leftEdgeBox, rightNode)
    // add the connections between left and right rooms
  }

  private def addVerticalConnections(splitPos: Int, topNode: BspNode[Room], bottomNode: BspNode[Room]): Unit = {
    val bottomEdgeBox = Box(splitPos - options.getMinPaddedDim, 0, splitPos, options.dimension.width)
    val topRooms = findRoomsGivenBoxFilter(bottomEdgeBox, topNode)
    val topEdgeBox = Box(splitPos, 0, splitPos + options.getMinPaddedDim, options.dimension.height)
    val bottomRooms = findRoomsGivenBoxFilter( topEdgeBox, bottomNode)
    // add the connections between top and bottom rooms
  }

  private def findRoomsGivenBoxFilter(boxFilter: Box, bottomNode: BspNode[Room]): Set[Room] = {
    Set()
  }

}
