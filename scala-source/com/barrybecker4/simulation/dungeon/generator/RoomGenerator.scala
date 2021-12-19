// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.RoomGenerator.ROOM_COLOR
import com.barrybecker4.simulation.dungeon.model.Room
import com.barrybecker4.simulation.dungeon.model.DungeonOptions

import java.awt.{Color, Dimension}
import scala.collection.immutable.HashSet
import scala.util.Random


object RoomGenerator {
  val ROOM_COLOR = new Color(100, 10, 200)
  var RND = Random(0)
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  val widthToHeightRatio: Float = options.dimension.width.toFloat / options.dimension.height.toFloat
  val boxSplitter = BoxSplitter(options.maxRoomWidth, options.maxRoomHeight)

  def generateRooms(): Set[Room] = {
    getRoomsForBox(Box(0, 0, options.dimension.height, options.dimension.width))
  }

  private def getRoomsForBox(box: Box): Set[Room] = {
    // base case if box small enough for a room, then return the room
    val border = options.roomPadding
    val cellSize = options.cellSize
    if (box.getWidth <= options.maxRoomWidth * cellSize && box.getHeight <= options.maxRoomHeight * cellSize) {
      val xpos = box.getTopLeftCorner.getX / cellSize + border
      val ypos = box.getTopLeftCorner.getY / cellSize + border
      val dim = Dimension(box.getWidth / cellSize - 2 * border, box.getHeight / cellSize  - 2 * border)

      val bigEnough = dim.width >= options.minRoomDim && dim.height >= options.minRoomDim
      if (rnd.nextInt(100) < options.percentFilled && bigEnough)
        HashSet(Room(IntLocation(ypos, xpos + options.roomPadding), dim, ROOM_COLOR))
      else HashSet()
    }
    else if (box.getWidth * widthToHeightRatio > box.getHeight) {
      val (leftBox, rightBox) = boxSplitter.splitHorizontally(box)
      getRoomsForBox(leftBox).union(getRoomsForBox(rightBox))
    }
    else {
      val (bottomBox, topBox) = boxSplitter.splitVertically(box)
      getRoomsForBox(bottomBox).union(getRoomsForBox(topBox))
    }
  }
}
