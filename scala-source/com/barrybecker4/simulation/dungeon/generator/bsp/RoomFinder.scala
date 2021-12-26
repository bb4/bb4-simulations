// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.Room

import scala.collection.immutable.HashSet
import PartitionDirection.*


class RoomFinder {

  /**
   * Recursively filter by the specified box
   * @return rooms that are at least partially in the box
   */
  def filterByBox(boxFilter: Box, bspNode: BspNode): Set[Room] = {

    bspNode match {
      case BspBranchNode(direction, splitPos, partition1, partition2) =>
        var rooms: Set[Room] = Set()
        if (direction == PartitionDirection.Horizontal) {
          if (boxFilter.getTopLeftCorner.getX < splitPos)
            rooms ++= filterByBox(boxFilter, partition1)
          if (boxFilter.getBottomRightCorner.getX > splitPos)
            rooms ++= filterByBox(boxFilter, partition2)
        } else {
          if (boxFilter.getTopLeftCorner.getY < splitPos)
            rooms ++= filterByBox(boxFilter, partition1)
          if (boxFilter.getBottomRightCorner.getY > splitPos)
            rooms ++= filterByBox(boxFilter, partition2)
        }
        rooms
      case BspLeafNode(room) => HashSet(room)
    }
  }
}
