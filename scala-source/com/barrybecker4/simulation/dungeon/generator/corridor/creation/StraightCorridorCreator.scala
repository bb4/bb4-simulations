// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.corridor.creation

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.bsp.PartitionDirection
import com.barrybecker4.simulation.dungeon.model.{Corridor, Room, RoomDecoration}
import com.barrybecker4.simulation.dungeon.generator.corridor.creation.CorridorCreatorConsts.*
import java.awt.Color



class StraightCorridorCreator {

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

    val overlapX = findLinearOverlap(leftX1, rightX1, leftX2, rightX2)
    val overlapY = findLinearOverlap(topY1, bottomY1, topY2, bottomY2)

    if (overlapX >= MIN_OVERLAP || overlapY >= MIN_OVERLAP) {
      val sortedX = Seq(leftX1, rightX1, leftX2, rightX2).sorted
      val sortedY = Seq(topY1, bottomY1, topY2, bottomY2).sorted
      Some(Box(sortedY(1), sortedX(1), sortedY(2), sortedX(2)))
    }
    else None
  }

  private def findLinearOverlap(a1: Int, a2: Int, b1: Int, b2: Int): Int = {
    val left = Math.max(a1, b1)
    val right = Math.min(a2, b2)
    if (left < right) right - left else 0
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
      path = Seq(IntLocation(box.getTopLeftCorner.getY, midX), IntLocation(box.getBottomRightCorner.getY, midX))
    }

    Corridor(path, CORRIDOR_DECORATION)
  }
}
