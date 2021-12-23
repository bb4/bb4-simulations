// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.processors

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.processors.RoomCorridorCreator.{CORRIDOR_DECORATION, MIN_OVERLAP}
import com.barrybecker4.simulation.dungeon.generator.bsp.PartitionDirection
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room, RoomDecoration}

import java.awt.Color


object RoomCorridorCreator {
  private val MIN_OVERLAP: Int = 3
  private val CORRIDOR_DECORATION = RoomDecoration(new Color(100, 0, 80), new Color(180, 90, 210, 70))
}

class RoomCorridorCreator {

  def createCorridorBetweenRooms(direction: PartitionDirection,
                                 room1: Room, room2: Room): Option[Corridor] = {
    val overlap: Option[Box] = findOverlap(room1, room2, MIN_OVERLAP)
    if (overlap.isEmpty) None
    else Some(findCorridorForOverlap(overlap.get, direction, room1, room2))
  }

  private def findOverlap(room1: Room, room2: Room, minOverlap: Int): Option[Box] = {
    val leftX1 = room1.box.getTopLeftCorner.getX
    val rightX1 = room1.box.getBottomRightCorner.getX
    val leftX2 = room2.box.getTopLeftCorner.getX
    val rightX2 = room2.box.getBottomRightCorner.getX

    val topY1 = room1.box.getTopLeftCorner.getY
    val bottomY1 = room1.box.getBottomRightCorner.getY
    val topY2 = room2.box.getTopLeftCorner.getY
    val bottomY2 = room2.box.getBottomRightCorner.getY

    val overlapInX = leftX1 < leftX2 && rightX1 > leftX2
      || leftX1 < rightX2 && rightX1 > rightX2
      || leftX1 > leftX2 && rightX1 < rightX2
    val overlapInY = topY1 < topY2 && bottomY1 > topY2
      || topY1 < bottomY2 && bottomY1 > bottomY2
      || topY1 > topY2 && bottomY1 < bottomY2

    if (overlapInX || overlapInY) {
      val sortedX = Seq(leftX1, rightX1, leftX2, rightX2).sorted
      val sortedY = Seq(topY1, bottomY1, topY2, bottomY2).sorted
      Some(Box(sortedY(1), sortedX(1), sortedY(2), sortedX(2)))
    }
    else None
  }

  private def findCorridorForOverlap(box: Box, direction: PartitionDirection,
                                     room1: Room, room2: Room): Corridor = {
    var path: Seq[IntLocation] = Seq()
    if (direction == PartitionDirection.Horizontal) {
      val midY = box.getTopLeftCorner.getY + box.getHeight / 2
      path = Seq(IntLocation(midY, box.getTopLeftCorner.getX), IntLocation(midY, box.getBottomRightCorner.getX))
    }
    else {
      val midX = box.getTopLeftCorner.getX + box.getWidth / 2
      path = Seq(IntLocation(box.getTopLeftCorner.getY, midX), IntLocation(box.getBottomRightCorner.getX, midX))
    }

    Corridor(path, CORRIDOR_DECORATION)
  }
}
