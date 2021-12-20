// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.RoomGenerator.*
import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Room, RoomDecoration}

import java.awt.{Color, Dimension}
import scala.collection.immutable.HashSet
import scala.util.Random


object RoomGenerator {
  val ROOM_DECORATION: RoomDecoration =
    RoomDecoration(new Color(110, 20, 210), new Color(100, 10, 200, 100))
  val DEBUG_ROOM_DECORATION: RoomDecoration =
    RoomDecoration(new Color(90, 90, 110, 220), new Color(90, 80, 90, 30))
  val RND: Random = Random(0)
  val DEBUG = true
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private val widthToHeightRatio: Float =
    options.dimension.width.toFloat / options.dimension.height.toFloat

  private val boxSplitter =
    BoxSplitter(options.maxRoomWidth, options.maxRoomHeight, options.minRoomDim)

  def generateRooms(): Set[Room] = {
    getRoomsForBox(Box(0, 0, options.dimension.height, options.dimension.width))
  }

  private def getRoomsForBox(box: Box): Set[Room] = {

    val border = options.roomPadding
    val border2 = 2 * border
    val cellSize = options.cellSize
    val minDim = options.minRoomDim

    val boxSmallEnough =
      (box.getWidth <= options.maxRoomWidth + border2 && box.getHeight <= options.maxRoomHeight + border2)

    if (boxSmallEnough) {        // base case of recursion
      val upperLeft = IntLocation(box.getTopLeftCorner.getY + border, box.getTopLeftCorner.getX + border)
      val bottomRight = IntLocation(upperLeft.getY + box.getHeight - border, upperLeft.getX + box.getWidth - border)
      val roomBox = new Box(upperLeft, bottomRight)

      val bigEnough = roomBox.getWidth >= minDim && roomBox.getHeight >= minDim

      if (rnd.nextInt(100) < options.percentFilled && bigEnough)
        HashSet(Room(roomBox, ROOM_DECORATION))
      else if (DEBUG) HashSet(Room(box, DEBUG_ROOM_DECORATION))
      else HashSet()
    }
    else if (box.getWidth * widthToHeightRatio > box.getHeight) {
      assert(box.getWidth >= 2 * minDim, "box dim = " + box.getWidth + ", " + box.getHeight + " "+ options)
      val (leftBox, rightBox) = boxSplitter.splitHorizontally(box)
      getRoomsForBox(leftBox).union(getRoomsForBox(rightBox))
    }
    else {
      assert(box.getHeight >= 2 * minDim, "box dim = " + box.getWidth + ", " + box.getHeight)
      val (bottomBox, topBox) = boxSplitter.splitVertically(box)
      getRoomsForBox(bottomBox).union(getRoomsForBox(topBox))
    }
  }
}
