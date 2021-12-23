// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.CorridorGenerator.*
import com.barrybecker4.simulation.dungeon.generator.bsp.{BspBranchNode, BspNode, PartitionDirection, RoomFinder}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, Room, RoomDecoration}

import java.awt.Color
import scala.collection.immutable.{HashMap, HashSet}


case class CorridorGenerator(options: DungeonOptions) {

  private var roomToCorridors: RoomToCorridorsMap = _
  private val roomFinder = RoomFinder()
  private val corridorCreator = RoomCorridorCreator()

  def generateCorridors(bspTree: BspNode[Room]): RoomToCorridorsMap = {
    roomToCorridors = RoomToCorridorsMap()
    addCorridorsToMap(bspTree)
    // consider adding corridors for all nearby rooms if connectivity above some threshold
    roomToCorridors
  }

  private def addCorridorsToMap(node: BspNode[Room]): Unit = {
    node match {
      case BspBranchNode(direction, splitPos, partition1, partition2) =>
        addCorridorsToMap(partition1)
        addCorridorsToMap(partition2)
        addCorridorsBetween(direction, splitPos, partition1, partition2)
      case _ => // intentionally do nothing
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
    // Might need to broaden the range searched for until wer find at least one room
    val rightEdgeBox = Box(0, splitPos - options.getMinPaddedDim, options.dimension.height, splitPos)
    val leftRooms = roomFinder.filterByBox(rightEdgeBox, leftNode)
    val leftEdgeBox = Box(0, splitPos, options.dimension.height, splitPos + options.getMinPaddedDim)
    val rightRooms = roomFinder.filterByBox(leftEdgeBox, rightNode)

    // add the connections between left and right rooms
    addCorridors(PartitionDirection.Horizontal, leftRooms, rightRooms)
  }

  private def addVerticalConnections(splitPos: Int, topNode: BspNode[Room], bottomNode: BspNode[Room]): Unit = {
    val bottomEdgeBox = Box(splitPos - options.getMinPaddedDim, 0, splitPos, options.dimension.width)
    val topRooms = roomFinder.filterByBox(bottomEdgeBox, topNode)
    val topEdgeBox = Box(splitPos, 0, splitPos + options.getMinPaddedDim, options.dimension.width)
    val bottomRooms = roomFinder.filterByBox(topEdgeBox, bottomNode)
    // add the connections between top and bottom rooms
    addCorridors(PartitionDirection.Vertical, topRooms, bottomRooms)
  }

  private def addCorridors(direction: PartitionDirection, rooms1: Set[Room], rooms2: Set[Room]): Unit = {
    // for each room in room1, look for a room on rooms2 that overlaps in y by at least 3 cells
    // add a corridor in the middle of the overlap
    for (room1 <- rooms1)
      for (room2 <- rooms2) {
        val corridor = corridorCreator.createCorridorBetweenRooms(direction, room1, room2)
        if (corridor.nonEmpty)  {
          roomToCorridors.addCorridorToMap(room1, corridor.get)
          roomToCorridors.addCorridorToMap(room2, corridor.get)
        }
      }
  }
}
