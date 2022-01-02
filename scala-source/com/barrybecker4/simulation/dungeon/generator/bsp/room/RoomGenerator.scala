// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.bsp.tree.{BspBranchNode, BspLeafNode, BspNode}
import RoomGenerator.*
import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Orientation, Room}

import java.awt.{Color, Dimension}
import scala.util.Random


object RoomGenerator {
  private val RND: Random = Random(0)
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private val padding = options.roomOptions.roomPadding
  private val roomOptions = options.roomOptions
  private val widthToHeightRatio: Float = 
    roomOptions.getMaxPaddedWidth.toFloat / roomOptions.getMaxPaddedHeight

  private val boxSplitter = BoxSplitter(roomOptions, rnd)

  def generateRooms(): BspNode = {
    val dim = options.dimension
    getRoomsForBox(Box(0, 0, dim.height, dim.width)).get
  }

  private def getRoomsForBox(box: Box): Option[BspNode] = {

    val padding2 = 2 * padding
    val cellSize = options.cellSize
    val minDim = roomOptions.minRoomDim
    val ratio = (box.getWidth + padding2).toFloat / (box.getHeight + padding2)

    if (smallEnough(box)) {        // base case of recursion
      val pos = box.getTopLeftCorner
      val upperLeft = IntLocation(pos.getY + padding, pos.getX + padding)
      val bottomRight =
        if (options.halfPadded) IntLocation(pos.getY + box.getHeight, pos.getX + box.getWidth)
        else IntLocation(pos.getY + box.getHeight - padding, pos.getX + box.getWidth - padding)
      val roomBox = new Box(upperLeft, bottomRight)

      val bigEnough = roomBox.getWidth >= minDim && roomBox.getHeight >= minDim

      if (rnd.nextInt(100) < roomOptions.percentFilled && bigEnough)
        Some(BspLeafNode(Room(roomBox)))
      else None
    }
    else if (ratio > widthToHeightRatio) {
      val (leftBox, rightBox) = boxSplitter.splitHorizontally(box)
      val leftNode = getRoomsForBox(leftBox)
      val rightNode = getRoomsForBox(rightBox)
      createNode(Orientation.Horizontal, leftBox.getBottomRightCorner.getX, leftNode, rightNode)
    }
    else {
      val (bottomBox, topBox) = boxSplitter.splitVertically(box)
      val topNode = getRoomsForBox(topBox)
      val bottomNode = getRoomsForBox(bottomBox)
      createNode(Orientation.Vertical, topBox.getBottomRightCorner.getY, topNode, bottomNode)
    }
  }

  /** If neither child has a room, then add an empty leaf instead of a branch node (pruned) */
  private def createNode(direction: Orientation, split: Int,
                         node1: Option[BspNode], node2: Option[BspNode]): Option[BspNode] = {
    if (node1.nonEmpty && node2.nonEmpty)
      Some(BspBranchNode(direction, split, node1.get, node2.get))
    else if (node1.nonEmpty) node1
    else if (node2.nonEmpty) node2
    else None // pruned
  }

  private def smallEnough(box: Box): Boolean =
    box.getWidth <= roomOptions.getMaxPaddedWidth && box.getHeight <= roomOptions.getMaxPaddedHeight

}
