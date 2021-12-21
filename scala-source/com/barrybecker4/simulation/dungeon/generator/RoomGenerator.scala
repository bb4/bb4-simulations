// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.RoomGenerator.*
import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Room, RoomDecoration}

import java.awt.{Color, Dimension}
import scala.collection.immutable.HashSet
import scala.util.Random


object RoomGenerator {
  private val ROOM_DECORATION: RoomDecoration =
    RoomDecoration(new Color(120, 0, 240), new Color(130, 30, 230, 50))
  private val DEBUG_ROOM_DECORATION: RoomDecoration =
    RoomDecoration(new Color(90, 90, 110, 220), new Color(90, 80, 90, 40))
  private val RND: Random = Random(0)
  private val DEBUG = true
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private val padding = options.roomPadding
  private val widthToHeightRatio: Float = options.getMaxPaddedWidth.toFloat / options.getMaxPaddedHeight

  private val boxSplitter =
    BoxSplitter(options.getMaxPaddedWidth, options.getMaxPaddedHeight, options.getMinPaddedDim)

  def generateRooms(): Set[Room] = {
    val dim = options.dimension
    getRoomsForBox(Box(0, 0, dim.height - padding, dim.width - padding))
  }

  private def getRoomsForBox(box: Box): Set[Room] = {

    val padding2 = 2 * padding
    val cellSize = options.cellSize
    val minDim = options.minRoomDim
    val ratio = (box.getWidth + padding2).toFloat / (box.getHeight + padding2)

    if (smallEnough(box)) {        // base case of recursion
      val upperLeft = IntLocation(box.getTopLeftCorner.getY + padding, box.getTopLeftCorner.getX + padding)
      val bottomRight = IntLocation(upperLeft.getY + box.getHeight - padding, upperLeft.getX + box.getWidth - padding)
      val roomBox = new Box(upperLeft, bottomRight)

      val bigEnough = roomBox.getWidth >= minDim && roomBox.getHeight >= minDim

      if (rnd.nextInt(100) < options.percentFilled && bigEnough)
        HashSet(Room(roomBox, ROOM_DECORATION))
      else if (DEBUG) HashSet(Room(box, DEBUG_ROOM_DECORATION))
      else HashSet()
    }
    else if (ratio > widthToHeightRatio) {
      println(s"rat=$ratio wToHeightRat=$widthToHeightRatio  w=${box.getWidth} h=${box.getHeight}")
      verifyDim(box.getWidth, box)
      val (leftBox, rightBox) = boxSplitter.splitHorizontally(box)
      getRoomsForBox(leftBox).union(getRoomsForBox(rightBox))
    }
    else {
      verifyDim(box.getHeight, box)
      val (bottomBox, topBox) = boxSplitter.splitVertically(box)
      getRoomsForBox(bottomBox).union(getRoomsForBox(topBox))
    }
  }

  private def verifyDim(boxDim: Int, box: Box): Unit = {
    assert(boxDim >= 2 * options.getMinPaddedDim,
      "box dim = " + boxDim + " < " + 2 * options.getMinPaddedDim + "\n " + box + " " + options
      + "\n widthToHeightRat=" + widthToHeightRatio)
  }

  private def smallEnough(box: Box): Boolean = {
    box.getWidth <= options.getMaxPaddedWidth && box.getHeight <= options.getMaxPaddedHeight
  }

}
