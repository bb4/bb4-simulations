package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.{RoomAndCorridor, SproutLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonMap, Path, Room}
import com.barrybecker4.simulation.dungeon.model.options.RoomOptions
import com.barrybecker4.simulation.dungeon.model.Orientation.*

import java.awt.Dimension
import scala.util.Random


object RandomRoomCreator {
  private val RND = Random(0)
}

case class RandomRoomCreator(roomOptions: RoomOptions,
                             bounds: Box, rnd: Random = RND) {

  private val minMargin = roomOptions.minRoomDim / 2
  private val minDim = roomOptions.minRoomDim + 1

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

  /**
   * The DungeonMap will allow us to see how far we can grow the
   * room without overlapping other features.
   * @return a room and attaching corridor (if possible to create one)
   *         from specified sproutLocation
   */
  def createRoomFromSproutLocation(sproutLocation: SproutLocation,
                                   dungeonMap: DungeonMap): Option[RoomAndCorridor] = {
    val candidateRoom = createMinSizedRoomFromSprout(sproutLocation, dungeonMap)
    val roomExpander = RoomExpander(dungeonMap)
    
    if (candidateRoom.isDefined) {
      val room = roomExpander.expand(candidateRoom.get, getRandomDim, sproutLocation)
      val path = Path(sproutLocation.position, sproutLocation.orientation, 1)
      val corridor = Corridor(Seq(path), Set(sproutLocation.room, room))
      return Some(sprout.RoomAndCorridor(room, corridor))
    }
    None
  }

  private def createMinSizedRoomFromSprout(location: SproutLocation,
                                           dungeonMap: DungeonMap): Option[Room] = {
    val boxes: (Box, Box) = location match {
      case SproutLocation(_, position, Horizontal, -1) =>
        (Box(position.getY - minMargin - 1, position.getX - minDim - 1, position.getY + minMargin + 2, position.getX),
        Box(position.getY - minMargin, position.getX - minDim + 1, position.getY + minMargin + 1, position.getX))
      case SproutLocation(_, position, Horizontal, 1) =>
        (Box(position.getY - minMargin - 1, position.getX + 1, position.getY + minMargin + 2, position.getX + minDim + 2),
          Box(position.getY - minMargin, position.getX + 1, position.getY + minMargin + 1, position.getX + minDim))
      case SproutLocation(_, position, Vertical, -1) =>
        (Box(position.getY - minDim - 1, position.getX - minMargin - 1, position.getY, position.getX + minMargin + 2),
          Box(position.getY - minDim + 1, position.getX - minMargin, position.getY, position.getX + minMargin + 1))
      case SproutLocation(_, position, Vertical, 1) =>
        (Box(position.getY + 1, position.getX - minMargin -1, position.getY + minDim + 2, position.getX + minMargin + 2),
          Box(position.getY + 1, position.getX - minMargin, position.getY + minDim, position.getX + minMargin + 1))
      case _ => throw IllegalStateException()
    }
    if (bounds.contains(boxes._1) && dungeonMap.isEmptyRegion(boxes._1)) {
      return Some(Room(boxes._2))
    }
    None
  }

  private def getRandomDim: Dimension =
     Dimension(minDim + rnd.nextInt(roomOptions.maxRoomWidth - minDim), minDim + rnd.nextInt(roomOptions.maxRoomHeight))
}
