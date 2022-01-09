package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.SproutLocation
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, Room}
import com.barrybecker4.simulation.dungeon.model.Orientation.*

import java.awt.Dimension
import scala.util.Random

object RoomExpander {
  private val RND = Random(0)
}

case class RoomExpander(dungeonMap: DungeonMap, bounds: Box, rnd: Random = RND) {

  /**
   * Incrementally expand the room until it hits something or reaches size preference,
   * then back up two cells to leave room for buffer between rooms.
   * @return a version of the room that has been expanded to as close to its desired size as we can get
   */
  def expand(room: Room, preferredDim: Dimension, sproutLocation: SproutLocation): Room = {
    var stillExpandingHorizontally = true
    var stillExpandingVertically = true
    var expandedRoom: Room = room

    while (stillExpandingHorizontally || stillExpandingVertically) {

      if (stillExpandingHorizontally) {
        stillExpandingHorizontally = false
        val newRoom = attemptToExpandHorizontally(expandedRoom, preferredDim.width, sproutLocation)
        if (newRoom.isDefined) {
          expandedRoom = newRoom.get
          stillExpandingHorizontally = true
        }
      }

      if (stillExpandingVertically) {
        stillExpandingVertically = false
        val newRoom = attemptToExpandVertically(expandedRoom, preferredDim.height, sproutLocation)
        if (newRoom.isDefined) {
          expandedRoom = newRoom.get
          stillExpandingVertically = true
        }
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

    val possibleRoom = if (attemptRight) attemptToExpandRight(box) else attemptToExpandLeft(box)

    possibleRoom
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

    if (sprout.orientation == Vertical) {
      // look 2 cells to the top or bottom depending on direction. If clear, expand by 1
      None
    } else {
      // Look 2 cells out in both vertical directions, if clear, exand by one in corresponding direction
      None
    }
  }

  private def canExpand(point1: IntLocation, point2: IntLocation): Boolean = {
    val midPoint = IntLocation((point1.getY + point2.getY) / 2, (point1.getX + point2.getX) / 2)
    val mapShowsFree = dungeonMap(point1).isEmpty && dungeonMap(midPoint).isEmpty && dungeonMap(point2).isEmpty
    val inBounds = bounds.contains(point1) && bounds.contains(point2)
    mapShowsFree && inBounds
  }
}
