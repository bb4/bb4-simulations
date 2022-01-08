package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.Room
import com.barrybecker4.simulation.dungeon.model.options.RoomOptions

import scala.util.Random

object RandomRoomCreator {
  private val RND = Random(0)
}

case class RandomRoomCreator(roomOptions: RoomOptions,
                             bounds: Box, rnd: Random = RND) {

  def createRoom(): Room = {
    val minDim = roomOptions.minRoomDim
    val minPaddedDim = roomOptions.getMinPaddedDim

    val maxWidth = roomOptions.getMaxPaddedWidth
    val maxHeight = roomOptions.getMaxPaddedHeight
    val minXPos = bounds.getTopLeftCorner.getX + minPaddedDim
    val maxXPos = Math.max(minXPos + minDim, bounds.getBottomRightCorner.getX - minPaddedDim)
    val minYPos = bounds.getTopLeftCorner.getY + minPaddedDim
    val maxYPos = Math.max(minYPos + minDim, bounds.getBottomRightCorner.getY - minPaddedDim)

    val xPos = minXPos + rnd.nextInt(Math.max(1, maxXPos - minXPos - minPaddedDim))
    val yPos = minYPos + rnd.nextInt(Math.max(1, maxYPos - minYPos - minPaddedDim))
    val width = minDim + rnd.nextInt(roomOptions.maxRoomWidth - minDim)
    val height = minDim + rnd.nextInt(roomOptions.maxRoomHeight - minDim)
    Room(Box(yPos, xPos, Math.min(maxYPos, yPos + height), Math.min(maxXPos, xPos + width)))
  }

  def createRoomFromSproutLocation(sproutLocation: SproutLocation): Option[RoomAndCorridor] = {
    None
  }
}
