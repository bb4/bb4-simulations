// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.corridor

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.bsp.*
import com.barrybecker4.simulation.dungeon.generator.corridor.CorridorGenerator.CONNECTIVITY_SCALE
import com.barrybecker4.simulation.dungeon.generator.corridor.creation.*
import com.barrybecker4.simulation.dungeon.generator.room.RoomToCorridorsMap
import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Room, Corridor}

import java.awt.Dimension


object CorridorGenerator {
  private val CONNECTIVITY_SCALE = 16
}

case class CorridorGenerator(options: DungeonOptions) {

  private var roomToCorridors: RoomToCorridorsMap = _
  private val roomFinder = RoomFinder()
  private val straightCorridorCreator = StraightCorridorCreator()
  private val angledCorridorCreator = AngledCorridorCreator()
  private val connectivityThresh = options.connectivity * CONNECTIVITY_SCALE
  private var count = 0

  def generateCorridors(bspTree: BspNode[Room]): RoomToCorridorsMap = {
    roomToCorridors = RoomToCorridorsMap()
    addCorridorsToMap(bspTree)
    roomToCorridors
  }

  private def addCorridorsToMap(node: BspNode[Room]): Unit = {
    node match {
      case BspBranchNode(direction, splitPos, partition1, partition2) =>
        addCorridorsToMap(partition1)
        addCorridorsToMap(partition2)
        addCorridorsBetween(direction, splitPos, partition1, partition2)
      case _ => // base case of recursion. Intentionally do nothing
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
    val leftRooms = getLeftRooms(splitPos, leftNode)
    val rightRooms = getRightRooms(splitPos, rightNode)
    addCorridors(PartitionDirection.Horizontal, leftRooms, rightRooms)
  }

  private def addVerticalConnections(splitPos: Int, topNode: BspNode[Room], bottomNode: BspNode[Room]): Unit = {
    val topRooms = getTopRooms(splitPos, topNode)
    val bottomRooms = getBottomRooms(splitPos, bottomNode)
    addCorridors(PartitionDirection.Vertical, topRooms, bottomRooms)
  }

  private def getLeftRooms(splitPos: Int, leftNode: BspNode[Room]): Set[Room] =
    getRoomsNearEdge(splitPos, leftNode, -1, rightEdgeBoxCreator)

  private def getRightRooms(splitPos: Int, rightNode: BspNode[Room]): Set[Room] =
    getRoomsNearEdge(splitPos, rightNode, 1, leftEdgeBoxCreator)

  private def getTopRooms(splitPos: Int, topNode: BspNode[Room]): Set[Room] =
    getRoomsNearEdge(splitPos, topNode, -1, bottomEdgeBoxCreator)

  private def getBottomRooms(splitPos: Int, bottomNode: BspNode[Room]): Set[Room] =
    getRoomsNearEdge(splitPos, bottomNode, 1, topEdgeBoxCreator)

  private def getRoomsNearEdge(splitPos: Int, node: BspNode[Room], sign: Int,
                               boxCreator: (Int, Int, Dimension) => Box): Set[Room] = {
    var edgeRooms: Set[Room] = Set()
    var split = splitPos
    // Keep moving the search box away from the edge until we find at least one room
    while (edgeRooms.isEmpty) {
      val edgeBox = boxCreator(split, options.getMinPaddedDim, options.dimension)
      edgeRooms = roomFinder.filterByBox(edgeBox, node)
      split = split + sign * options.getMinPaddedDim
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
    count = 0
    val unmatchedRooms = addStraightCorridors(direction, rooms1, rooms2)

    if (count < connectivityThresh)
      addAngledCorridors(direction, unmatchedRooms, rooms1, rooms2)
  }

  /**
   * For each room in rooms1, look for a room on rooms2 that overlaps in y by at least 3 cells.
   * Add a corridor in the middle of the overlap.
   */
  private def addStraightCorridors(direction: PartitionDirection,
                                   rooms1: Set[Room], rooms2: Set[Room]): (Set[Room], Set[Room]) = {
    var matchedRooms1: Set[Room] = Set()
    var matchedRooms2: Set[Room] = Set()

    for (room1 <- rooms1) {
      for (room2 <- rooms2) {
        val corridor = straightCorridorCreator.createCorridorBetweenRooms(direction, room1, room2)
        if (corridor.nonEmpty) {
          count += 1
          if (count <= connectivityThresh) {
            addCorridorToMap(room1, room2, corridor.get)
            matchedRooms1 += room1
            matchedRooms2 += room2
          }
        }
      }
    }
    (rooms1.diff(matchedRooms1), rooms2.diff(matchedRooms2))
  }

  // for the unmatched rooms, we need corridors with turns
  private def addAngledCorridors(direction: PartitionDirection,
                                 unmatchedRooms: (Set[Room], Set[Room]),
                                 rooms1: Set[Room], rooms2: Set[Room]): Unit = {
    println(s"unmatched rooms1 = ${unmatchedRooms._1.size}")
    println(s"unmatched rooms2 = ${unmatchedRooms._2.size}")

    for (room1 <- unmatchedRooms._1) {
      val room2 = findClosestRoom(direction, room1, rooms2)
      count += 1
      if (count < connectivityThresh)
        addAngledCorridor(direction, room1, room2)
    }
    for (room2 <- unmatchedRooms._2) {
      val room1 = findClosestRoom(direction, room2, rooms1)
      count += 1
      if (count < connectivityThresh)
        addAngledCorridor(direction, room1, room2)
    }
  }

  private def findClosestRoom(direction: PartitionDirection, room: Room, rooms: Set[Room]): Room = {
    val centerFun: Room => Int =
      if (direction == PartitionDirection.Horizontal)
        room => room.box.getMinCol + room.box.getWidth / 2
      else
        room => room.box.getMinRow + room.box.getHeight / 2

    val centerPos = centerFun(room)
    val distanceFun = Math.abs(centerPos - centerFun(room))
    val roomsWithDistance = rooms.map(room => (distanceFun, room))

    roomsWithDistance.minBy(_._1)._2
  }

  private def addAngledCorridor(direction: PartitionDirection, room1: Room, room2: Room): Unit = {
    val corridor = angledCorridorCreator.createCorridorBetweenRooms(direction, room1, room2)
    if (corridor.nonEmpty)
      addCorridorToMap(room1, room2, corridor.get)
  }

  private def addCorridorToMap(room1: Room, room2: Room, corridor: Corridor): Unit = {
    roomToCorridors.addCorridorToMap(room1, corridor)
    roomToCorridors.addCorridorToMap(room2, corridor)
  }

}
