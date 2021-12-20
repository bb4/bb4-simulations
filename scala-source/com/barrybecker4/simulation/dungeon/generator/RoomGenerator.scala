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
    RoomDecoration(new Color(120, 0, 240), new Color(130, 30, 230, 50))
  val DEBUG_ROOM_DECORATION: RoomDecoration =
    RoomDecoration(new Color(90, 90, 110, 220), new Color(90, 80, 90, 40))
  val RND: Random = Random(0)
  val DEBUG = true
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private val widthToHeightRatio: Float =
    options.dimension.width.toFloat / options.dimension.height.toFloat

  private val padding = options.roomPadding
  private val boxSplitter =
    BoxSplitter(options.maxRoomWidth + padding, options.maxRoomHeight + padding, options.minRoomDim + padding)

  def generateRooms(): Set[Room] = {
    val dim = options.dimension
    getRoomsForBox(Box(0, 0, dim.height - padding, dim.width - padding))
  }

  private def getRoomsForBox(box: Box): Set[Room] = {
    
    val padding2 = 2 * padding
    val cellSize = options.cellSize
    val minDim = options.minRoomDim

    val boxSmallEnough =
      (box.getWidth <= options.maxRoomWidth + padding2 && box.getHeight <= options.maxRoomHeight + padding2)

    if (boxSmallEnough) {        // base case of recursion
      val upperLeft = IntLocation(box.getTopLeftCorner.getY + padding, box.getTopLeftCorner.getX + padding)
      val bottomRight = IntLocation(upperLeft.getY + box.getHeight - padding, upperLeft.getX + box.getWidth - padding)
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
