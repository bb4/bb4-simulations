// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.bsp.{BspBranchNode, BspLeafNode, BspNode, PartitionDirection}
import com.barrybecker4.simulation.dungeon.generator.room.RoomGenerator.*
import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Room, RoomDecoration}

import java.awt.{Color, Dimension}
import scala.collection.immutable.HashSet
import scala.util.Random


object RoomGenerator {
  private val ROOM_DECORATION: RoomDecoration =
    RoomDecoration(new Color(100, 0, 230), new Color(230, 190, 255))
  private val RND: Random = Random(0)
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private val padding = options.roomPadding
  private val widthToHeightRatio: Float = options.getMaxPaddedWidth.toFloat / options.getMaxPaddedHeight

  private val boxSplitter =
    BoxSplitter(options.getMaxPaddedWidth, options.getMaxPaddedHeight, options.getMinPaddedDim)

  def generateRooms(): BspNode = {
    val dim = options.dimension
    getRoomsForBox(Box(0, 0, dim.height, dim.width)).get
  }

  private def getRoomsForBox(box: Box): Option[BspNode] = {

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

      if (rnd.nextInt(100) < options.percentFilled && bigEnough)
        Some(BspLeafNode(Room(roomBox, ROOM_DECORATION)))
      else None
    }
    else if (ratio > widthToHeightRatio) {
      val (leftBox, rightBox) = boxSplitter.splitHorizontally(box)
      val leftNode = getRoomsForBox(leftBox)
      val rightNode = getRoomsForBox(rightBox)
      createNode(PartitionDirection.Horizontal, leftBox.getBottomRightCorner.getX, leftNode, rightNode)
    }
    else {
      val (bottomBox, topBox) = boxSplitter.splitVertically(box)
      val topNode = getRoomsForBox(topBox)
      val bottomNode = getRoomsForBox(bottomBox)
      createNode(PartitionDirection.Vertical, topBox.getBottomRightCorner.getY, topNode, bottomNode)
    }
  }

  /** If neither child has a room, then add an empty leaf instead of a branch node (pruned) */
  private def createNode(direction: PartitionDirection, split: Int,
                     node1: Option[BspNode], node2: Option[BspNode]): Option[BspNode] = {
    if (node1.nonEmpty && node2.nonEmpty)
      Some(BspBranchNode(direction, split, node1.get, node2.get))
    else if (node1.nonEmpty) node1
    else if (node2.nonEmpty) node2
    else None // pruned
  }

  private def smallEnough(box: Box): Boolean =
    box.getWidth <= options.getMaxPaddedWidth && box.getHeight <= options.getMaxPaddedHeight

}
