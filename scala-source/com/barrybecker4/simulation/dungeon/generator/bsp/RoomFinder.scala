// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.Room

import scala.collection.immutable.HashSet
import PartitionDirection.*


class RoomFinder {

  def filterByBox(boxFilter: Box, bspNode: BspNode[Room]): Set[Room] = {
    var rooms: Set[Room] = Set()
    bspNode.direction match {
      case Some(Horizontal) => {
        if (boxFilter.getTopLeftCorner.getX < bspNode.splitPosition.get) {
          rooms ++= filterByBox(boxFilter, bspNode.partition1.get)
        }
        if (boxFilter.getBottomRightCorner.getX > bspNode.splitPosition.get) {
          rooms ++= filterByBox(boxFilter, bspNode.partition2.get)
        }
      }
      case Some(Vertical) => {
        if (boxFilter.getTopLeftCorner.getY < bspNode.splitPosition.get) {
          rooms ++= filterByBox(boxFilter, bspNode.partition1.get)
        }
        if (boxFilter.getBottomRightCorner.getY > bspNode.splitPosition.get) {
          rooms ++= filterByBox(boxFilter, bspNode.partition2.get)
        }
      }
      case None => rooms = HashSet(bspNode.data.get)
    }
    rooms
  }
}
