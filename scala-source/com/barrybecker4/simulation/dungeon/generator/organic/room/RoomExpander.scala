// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.SproutLocation
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, Room}
import com.barrybecker4.simulation.dungeon.model.Orientation.*
import com.barrybecker4.simulation.dungeon.model.options.RoomOptions

import java.awt.Dimension
import scala.util.Random

object RoomExpander {
  private val RND = Random(0)
}

case class RoomExpander(dungeonMap: DungeonMap, roomOptions: RoomOptions, bounds: Box, rnd: Random = RND) {

  private val minDim = roomOptions.minRoomDim

  /**
   * Incrementally expand the room until it hits something or reaches size preference,
   * then back up two cells to leave room for buffer between rooms.
   * @return a version of the room that has been expanded to as close to its desired size as we can get
   */
  def expand(room: Room, preferredDim: Dimension, sproutLocation: SproutLocation): Room = {
    var expandedRoom: Room = room
    var previousRoom: Room = null

    while (expandedRoom != previousRoom) {
      previousRoom = expandedRoom
      val newHorizontalRoom = attemptToExpandHorizontally(expandedRoom, preferredDim.width, sproutLocation)
      if (newHorizontalRoom.isDefined) {
        expandedRoom = newHorizontalRoom.get
      }
      val newVerticalRoom = attemptToExpandVertically(expandedRoom, preferredDim.height, sproutLocation)
      if (newVerticalRoom.isDefined) {
        expandedRoom = newVerticalRoom.get
      }
    }
    expandedRoom
  }

  private def attemptToExpandHorizontally(room: Room, preferredWidth: Int, sprout: SproutLocation): Option[Room] = {
    val box = room.box
    if (box.getWidth + 1 >= preferredWidth)
      return None

    val attemptRight =
      if (sprout.orientation == Horizontal) sprout.direction == 1
      else (rnd.nextDouble() < 0.5)

    if (attemptRight) attemptToExpandRight(box) else attemptToExpandLeft(box)
  }

  private def attemptToExpandRight(box: Box): Option[Room] = {
    val rightX = box.getBottomRightCorner.getX
    val topExpandedPt = IntLocation(box.getTopLeftCorner.getY - 1, rightX + 2)
    val bottomExpandedPt = IntLocation(box.getBottomRightCorner.getY + 1, rightX + 2)
    if (canExpand(topExpandedPt, bottomExpandedPt))
      Some(Room(new Box(box.getTopLeftCorner, IntLocation(box.getBottomRightCorner.getY, rightX + 1))))
    else None
  }

  private def attemptToExpandLeft(box: Box): Option[Room] = {
    val leftX = box.getTopLeftCorner.getX
    val topExpandedPt = IntLocation(box.getTopLeftCorner.getY - 1, leftX - 2)
    val bottomExpandedPt = IntLocation(box.getBottomRightCorner.getY + 1, leftX - 2)
    if (canExpand(topExpandedPt, bottomExpandedPt))
      Some(Room(new Box(IntLocation(box.getTopLeftCorner.getY, leftX - 1), box.getBottomRightCorner)))
    else None
  }

  private def attemptToExpandVertically(room: Room, preferredHeight: Int, sprout: SproutLocation): Option[Room] = {
    val box = room.box
    if (box.getHeight + 1 >= preferredHeight)
      return None

    val attemptBottom =
      if (sprout.orientation == Vertical) sprout.direction == 1
      else (rnd.nextDouble() < 0.5)

    if (attemptBottom) attemptToExpandBottom(box) else attemptToExpandTop(box)
  }

  private def attemptToExpandBottom(box: Box): Option[Room] = {
    val bottomY = box.getBottomRightCorner.getY
    val leftExpandedPt = IntLocation(bottomY + 2, box.getTopLeftCorner.getX - 1)
    val rightExpandedPt = IntLocation(bottomY + 2, box.getBottomRightCorner.getX + 1)
    if (canExpand(leftExpandedPt, rightExpandedPt))
      Some(Room(new Box(box.getTopLeftCorner, IntLocation(bottomY + 1, box.getBottomRightCorner.getX))))
    else None
  }

  private def attemptToExpandTop(box: Box): Option[Room] = {
    val topY = box.getTopLeftCorner.getY
    val leftExpandedPt = IntLocation(topY - 2, box.getTopLeftCorner.getX - 1)
    val rightExpandedPt = IntLocation(topY - 2, box.getBottomRightCorner.getX + 1)
    if (canExpand(leftExpandedPt, rightExpandedPt))
      Some(Room(new Box(IntLocation(topY - 1, box.getTopLeftCorner.getX), box.getBottomRightCorner)))
    else None
  }

  private def canExpand(point1: IntLocation, point2: IntLocation): Boolean = {
    var edgePoints = List(point1, point2)
    if (point1.getX == point2.getX) {
      for (y <- point1.getY + minDim until point2.getY by minDim)
        edgePoints :+= IntLocation(y, point1.getX)
    } else {
      for (x <- point1.getX + minDim until point2.getX by minDim)
        edgePoints :+= IntLocation(point1.getY, x)
    }

    val occupied = edgePoints.exists(pt => dungeonMap(pt).nonEmpty)
    val inBounds = bounds.contains(point1) && bounds.contains(point2)
    !occupied && inBounds
  }
}
