// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp.corridor

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.bsp.tree.{BspBranchNode, BspNode, PartitionDirection}
import CorridorGenerator.CONNECTIVITY_SCALE
import com.barrybecker4.simulation.dungeon.generator.bsp.room.RoomToCorridorsMap
import com.barrybecker4.simulation.dungeon.generator.bsp.corridor.CorridorGenerator.*
import com.barrybecker4.simulation.dungeon.generator.bsp.tree.RoomFinder
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonOptions, Room}

import java.awt.Dimension


object CorridorGenerator {
  private val CONNECTIVITY_SCALE = 12
}

case class CorridorGenerator(options: DungeonOptions) {

  private var roomToCorridors: RoomToCorridorsMap = _
  private val roomFinder = RoomFinder()
  private val straightCorridorCreator = StraightCorridorCreator()
  private val connectivityThresh = options.connectivity * CONNECTIVITY_SCALE

  def generateCorridors(bspTree: BspNode): RoomToCorridorsMap = {
    roomToCorridors = RoomToCorridorsMap()
    addCorridorsToMap(bspTree)
    roomToCorridors
  }

  private def addCorridorsToMap(node: BspNode): Unit = {
    node match {
      case BspBranchNode(direction, splitPos, partition1, partition2) =>
        addCorridorsToMap(partition1)
        addCorridorsToMap(partition2)
        addCorridorsBetween(direction, splitPos, partition1, partition2)
      case _ => // base case of recursion. Intentionally do nothing
    }
  }

  private def addCorridorsBetween(direction: PartitionDirection, splitPos: Int,
                                  node1: BspNode, node2: BspNode): Unit = {
    if (direction == PartitionDirection.Horizontal)
      addHorizontalConnections(splitPos, node1, node2)
    else
      addVerticalConnections(splitPos, node1, node2)
  }

  private def addHorizontalConnections(splitPos: Int, leftNode: BspNode, rightNode: BspNode): Unit = {
    val leftRooms = getLeftRooms(splitPos, leftNode)
    val rightRooms = getRightRooms(splitPos, rightNode)
    addCorridors(PartitionDirection.Horizontal, leftRooms, rightRooms)
  }

  private def addVerticalConnections(splitPos: Int, topNode: BspNode, bottomNode: BspNode): Unit = {
    val topRooms = getTopRooms(splitPos, topNode)
    val bottomRooms = getBottomRooms(splitPos, bottomNode)
    addCorridors(PartitionDirection.Vertical, topRooms, bottomRooms)
  }

  private def getLeftRooms(splitPos: Int, leftNode: BspNode): Set[Room] =
    getRoomsNearEdge(splitPos, leftNode, -1, rightEdgeBoxCreator)

  private def getRightRooms(splitPos: Int, rightNode: BspNode): Set[Room] =
    getRoomsNearEdge(splitPos, rightNode, 1, leftEdgeBoxCreator)

  private def getTopRooms(splitPos: Int, topNode: BspNode): Set[Room] =
    getRoomsNearEdge(splitPos, topNode, -1, bottomEdgeBoxCreator)

  private def getBottomRooms(splitPos: Int, bottomNode: BspNode): Set[Room] =
    getRoomsNearEdge(splitPos, bottomNode, 1, topEdgeBoxCreator)

  private def getRoomsNearEdge(splitPos: Int, node: BspNode, sign: Int,
                               boxCreator: (Int, Int, Dimension) => Box): Set[Room] = {
    var edgeRooms: Set[Room] = Set()
    var split = splitPos
    val minPaddedDim = options.roomOptions.getMinPaddedDim
    // Keep moving the search box away from the edge until we find at least one room
    while (edgeRooms.isEmpty) {
      val edgeBox = boxCreator(split, minPaddedDim, options.dimension)
      edgeRooms = roomFinder.filterByBox(edgeBox, node)
      split = split + sign * minPaddedDim
    }
    edgeRooms
  }

  private def rightEdgeBoxCreator(split: Int, margin: Int, dim: Dimension): Box =
    Box(0, split - margin, dim.height, split)

  private def leftEdgeBoxCreator(split: Int, margin: Int, dim: Dimension): Box =
    Box(0, split, dim.height, split + margin)

  private def bottomEdgeBoxCreator(split: Int, margin: Int, dim: Dimension): Box =
    Box(split - margin, 0, split, dim.width)

  private def topEdgeBoxCreator(split: Int, margin: Int, dim: Dimension): Box =
    Box(split, 0, split + margin, dim.width)

  /**
   * Add corridors between rooms in the 2 partitions.
   * In some rare cases, we may need angled corridors
   */
  private def addCorridors(direction: PartitionDirection, rooms1: Set[Room], rooms2: Set[Room]): Unit = {
    var count = 0
    for (room1 <- rooms1) {
      for (room2 <- rooms2) {
        val corridor = straightCorridorCreator.createCorridorBetweenRooms(direction, room1, room2)
        if (corridor.nonEmpty) {
          count += 1
          if (count <= connectivityThresh) {
            addCorridorToMap(room1, room2, corridor.get)
          }
        }
      }
    }
  }

  private def addCorridorToMap(room1: Room, room2: Room, corridor: Corridor): Unit = {
    roomToCorridors.addCorridorToMap(room1, corridor)
    roomToCorridors.addCorridorToMap(room2, corridor)
  }

}
