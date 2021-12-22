// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.RoomGenerator.*
import com.barrybecker4.simulation.dungeon.generator.bsp.{PartitionDirection, BoxSplitter, BspTree, BspNode}
import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Room, RoomDecoration}

import java.awt.{Color, Dimension}
import scala.collection.immutable.HashSet
import scala.util.Random


object RoomGenerator {
  private val ROOM_DECORATION: RoomDecoration =
    RoomDecoration(new Color(100, 0, 230), new Color(130, 30, 230, 60))
  private val DEBUG_ROOM_DECORATION: RoomDecoration =
    RoomDecoration(new Color(90, 90, 110, 80), new Color(90, 80, 90, 10))
  private val RND: Random = Random(0)
  private val DEBUG = true
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private val padding = options.roomPadding
  private val widthToHeightRatio: Float = options.getMaxPaddedWidth.toFloat / options.getMaxPaddedHeight

  private val boxSplitter =
    BoxSplitter(options.getMaxPaddedWidth, options.getMaxPaddedHeight, options.getMinPaddedDim)

  def generateRooms(): BspTree[Room] = {
    val dim = options.dimension
    BspTree[Room](getRoomsForBox(Box(0, 0, dim.height, dim.width)))
  }

  private def getRoomsForBox(box: Box): BspNode[Room] = {

    val padding2 = 2 * padding
    val cellSize = options.cellSize
    val minDim = options.minRoomDim
    val ratio = (box.getWidth + padding2).toFloat / (box.getHeight + padding2)

    if (smallEnough(box)) {        // base case of recursion
      val pos = box.getTopLeftCorner
      val upperLeft = IntLocation(pos.getY + padding, pos.getX + padding)
      val bottomRight =
        if (options.halfPadded) IntLocation(pos.getY + box.getHeight, pos.getX + box.getWidth)
        else IntLocation(pos.getY + box.getHeight - padding, pos.getX + box.getWidth - padding)
      val roomBox = new Box(upperLeft, bottomRight)

      val bigEnough = roomBox.getWidth >= minDim && roomBox.getHeight >= minDim

      val room = if (rnd.nextInt(100) < options.percentFilled && bigEnough)
        Some(Room(roomBox, ROOM_DECORATION))
      else if (DEBUG) Some(Room(box, DEBUG_ROOM_DECORATION))
      else None

      BspNode[Room](None, None, None, None, room)
    }
    else if (ratio > widthToHeightRatio) {
      verifyDim(box.getWidth, box)
      val (leftBox, rightBox) = boxSplitter.splitHorizontally(box)
      new BspNode(PartitionDirection.Horizontal, leftBox.getBottomRightCorner.getX,
        getRoomsForBox(leftBox), getRoomsForBox(rightBox))
    }
    else {
      verifyDim(box.getHeight, box)
      val (bottomBox, topBox) = boxSplitter.splitVertically(box)
      new BspNode(PartitionDirection.Vertical, topBox.getBottomRightCorner.getY,
        getRoomsForBox(topBox), getRoomsForBox(bottomBox))
    }
  }

  private def verifyDim(boxDim: Int, box: Box): Unit = {
    assert(boxDim >= 2 * options.getMinPaddedDim,
      "box dim = " + boxDim + " < " + 2 * options.getMinPaddedDim + "\n " + box + " " + options
      + "\n widthToHeightRat=" + widthToHeightRatio)
  }

  private def smallEnough(box: Box): Boolean =
    box.getWidth <= options.getMaxPaddedWidth && box.getHeight <= options.getMaxPaddedHeight

}
