// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.organic.room.RandomRoomCreator.adjustToMaxAspect
import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.{RoomAndCorridor, SproutLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonMap, Path, Room}
import com.barrybecker4.simulation.dungeon.model.options.RoomOptions
import com.barrybecker4.simulation.dungeon.model.Orientation.*

import java.awt.Dimension
import scala.util.Random


object RandomRoomCreator {
  private val RND = Random(0)

  def adjustToMaxAspect(width: Int, height: Int, maxAspectRatio: Float): Dimension = {
    var adjWidth = width
    var adjHeight = height

    if ((width.toFloat / height) > maxAspectRatio)
      adjWidth = (height * maxAspectRatio).toInt
    else if ((height.toFloat / width) > maxAspectRatio)
      adjHeight = (width * maxAspectRatio).toInt

    Dimension(adjWidth, adjHeight)
  }
}

case class RandomRoomCreator(roomOptions: RoomOptions,
                             bounds: Box, rnd: Random = RND) {

  private val minMargin = roomOptions.minRoomDim / 2
  private val minDim = roomOptions.minRoomDim + 1
  private val maxAspectRatio = roomOptions.maxAspectRatio
  private val padding = roomOptions.roomPadding

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
    
    val dim = adjustToMaxAspect(width, height, maxAspectRatio)
    Room(Box(yPos, xPos, Math.min(maxYPos, yPos + dim.height), Math.min(maxXPos, xPos + dim.width)))
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
    val roomExpander = RoomExpander(dungeonMap, roomOptions, bounds, rnd)
    
    if (candidateRoom.isDefined) {
      val room = roomExpander.expand(candidateRoom.get, getRandomDim, sproutLocation)
      val path = createPath(sproutLocation)
      val corridor = Corridor(Seq(path), Set(sproutLocation.room, room))
      return Some(sprout.RoomAndCorridor(room, corridor))
    }
    None
  }

  private def createPath(sproutLocation: SproutLocation): Path = {
    val pos = sproutLocation.position

    if (sproutLocation.direction == -1) {
      sproutLocation.orientation match {
        case Horizontal => Path(IntLocation(pos.getY, pos.getX - padding + 1), Horizontal, padding)
        case Vertical => Path(IntLocation(pos.getY - padding + 1, pos.getX), Vertical, padding)
      }
    }
    else Path(pos, sproutLocation.orientation, padding)
  }

  private def createMinSizedRoomFromSprout(location: SproutLocation,
                                           dungeonMap: DungeonMap): Option[Room] = {
    case class Candidate(bounds: Box, room: Box)

    val candidate: Candidate = location match {
      case SproutLocation(_, position, Horizontal, -1) => Candidate(
        Box(position.getY - minMargin - padding, position.getX - minDim - 2 * padding, position.getY + minMargin + padding + 1, position.getX),
        Box(position.getY - minMargin, position.getX - minDim - padding + 1, position.getY + minMargin + 1, position.getX - padding + 1))
      case SproutLocation(_, position, Horizontal, 1) => Candidate(
        Box(position.getY - minMargin - padding, position.getX + 1, position.getY + minMargin + padding + 1, position.getX + minDim + 2 * padding),
        Box(position.getY - minMargin, position.getX + padding, position.getY + minMargin + 1, position.getX + minDim + padding - 1))
      case SproutLocation(_, position, Vertical, -1) => Candidate(
        Box(position.getY - minDim - 2 * padding, position.getX - minMargin - padding, position.getY, position.getX + minMargin + padding + 1),
        Box(position.getY - minDim - padding + 1, position.getX - minMargin, position.getY - padding + 1, position.getX + minMargin + 1))
      case SproutLocation(_, position, Vertical, 1) => Candidate(
        Box(position.getY + 1, position.getX - minMargin - padding, position.getY + minDim + 2 * padding + 1, position.getX + minMargin + padding + 1),
        Box(position.getY + padding, position.getX - minMargin, position.getY + minDim + 2 * padding, position.getX + minMargin + 1))
      case _ => throw IllegalStateException()
    }
    if (bounds.contains(candidate.bounds) && dungeonMap.isEmptyRegion(candidate.bounds)) {
      return Some(Room(candidate.room))
    }
    None
  }

  private def getRandomDim: Dimension = {
    val rWidth = minDim + rnd.nextInt(roomOptions.maxRoomWidth - minDim)
    val rHeight = minDim + rnd.nextInt(roomOptions.maxRoomHeight - minDim)
    adjustToMaxAspect(rWidth, rHeight, maxAspectRatio)
  }

}
