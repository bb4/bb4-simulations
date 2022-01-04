package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.common.util.SkewedRandom
import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.BoxDecomposer.RND
import com.barrybecker4.simulation.dungeon.model.Room
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions

import scala.collection.immutable.HashSet
import scala.util.Random


object BoxDecomposer {
  private val RND: Random = Random(0)
}

case class BoxDecomposer(options: DungeonOptions, rnd: Random = RND) {

  private val roomOptions = options.roomOptions
  private val roomPadding = roomOptions.roomPadding
  private val padding = if (options.halfPadded) roomPadding else 2 * roomPadding
  private val frontPadding = if (options.halfPadded) 0 else roomPadding
  private val RATIO = roomOptions.getMaxPaddedWidth / roomOptions.getMaxPaddedHeight
  private val maxPaddedWidth = roomOptions.getMaxPaddedWidth
  private val maxPaddedHeight = roomOptions.getMaxPaddedHeight
  private val skewedRandom: SkewedRandom = SkewedRandom(rnd)
  
  private val skew = options.roomOptions.randomSkew
  private val bias = options.roomOptions.randomBias

  /**
   * If half-padded, then the padding only goes at the bottom
   * @param box the box to find a room in
   * @return the room and a set of boxes that represents the area outside the box
   */
  def createRoomInBox(box: Box): (Room, Set[Box]) = {
    val width = randomWidth(box)
    val height = randomHeight(box)
    val topLeft = box.getTopLeftCorner
    val rWidth = box.getWidth - width - padding
    val rHeight = box.getHeight - height - padding
    val xPos = topLeft.getX + frontPadding + (if (rWidth == 0) 0 else rnd.nextInt(rWidth))
    val yPos = topLeft.getY + frontPadding + (if (rHeight == 0) 0 else rnd.nextInt(rHeight))

    val roomBox = Box(yPos, xPos, yPos + height, xPos + width)
    val roomContainer = Box(yPos - frontPadding, xPos - frontPadding, yPos + height + roomPadding, xPos + width + roomPadding)
    val room: Room = Room(roomBox)
    assert(roomContainer.contains(roomBox.getTopLeftCorner) && roomContainer.contains(roomBox.getBottomRightCorner))

    val areas = findUnoccupiedAreas(box, roomContainer)
    (room, areas)
  }

  private def findUnoccupiedAreas(box: Box, innerContainer: Box): Set[Box] = {
    assert(box.contains(innerContainer.getTopLeftCorner) && box.contains(innerContainer.getBottomRightCorner))

    if (box == innerContainer)
      return HashSet[Box]()

    val divideHorizontally = box.getHeight == innerContainer.getHeight
      || (getBoxRatio(box, innerContainer) > RATIO)
    val boxTopLeft = box.getTopLeftCorner
    val boxBottomRight = box.getBottomRightCorner
    val innerTopLeft = innerContainer.getTopLeftCorner
    val innerBottomRight = innerContainer.getBottomRightCorner
    if (divideHorizontally) {
      val leftWider = innerTopLeft.getX - boxTopLeft.getX > boxBottomRight.getX - innerBottomRight.getX
      val shavedBox = if (leftWider) new Box(boxTopLeft, IntLocation(boxBottomRight.getY, innerTopLeft.getX))
                      else new Box(IntLocation(boxTopLeft.getY, innerBottomRight.getX), boxBottomRight)
      val newBox = if (leftWider) new Box(IntLocation(boxTopLeft.getY, innerTopLeft.getX), boxBottomRight)
                   else new Box(boxTopLeft, IntLocation(boxBottomRight.getY, innerBottomRight.getX))

      assert(newBox.contains(innerContainer.getTopLeftCorner) && newBox.contains(innerContainer.getBottomRightCorner))
      findUnoccupiedAreas(newBox, innerContainer) + shavedBox
    } else {
      assert(box.getHeight > innerContainer.getHeight, s"inner ht (${innerContainer.getHeight}) was larger than ht (${box.getHeight})")
      val topTaller = innerTopLeft.getY - boxTopLeft.getY > boxBottomRight.getY - innerBottomRight.getY
      val shavedBox = if (topTaller) new Box(boxTopLeft, IntLocation(innerTopLeft.getY, boxBottomRight.getX))
                      else new Box(IntLocation(innerBottomRight.getY, boxTopLeft.getX), boxBottomRight)
      val newBox = if (topTaller) new Box(IntLocation(innerTopLeft.getY, boxTopLeft.getX), boxBottomRight)
                   else new Box(boxTopLeft, IntLocation(innerBottomRight.getY, boxBottomRight.getX))

      assert(newBox.contains(innerContainer.getTopLeftCorner) && newBox.contains(innerContainer.getBottomRightCorner))
      findUnoccupiedAreas(newBox, innerContainer) + shavedBox
    }
  }

  private def getBoxRatio(box: Box, innerContainer: Box): Float =
    (box.getWidth - innerContainer.getWidth).toFloat / (box.getHeight - innerContainer.getHeight)

  private def randomWidth(box: Box): Int =
    if (box.getWidth < maxPaddedWidth) box.getWidth - padding
    else Math.min(box.getWidth - padding, randomDim(roomOptions.maxRoomWidth))

  private def randomHeight(box: Box): Int =
    if (box.getHeight < maxPaddedHeight) box.getHeight - padding
    else Math.min(box.getHeight - padding, randomDim(roomOptions.maxRoomHeight))

  private def randomDim(maxDim: Int): Int =
    skewedRandom.nextSkewedGaussian(roomOptions.minRoomDim, maxDim - roomOptions.minRoomDim, skew, bias).toInt
    //roomOptions.minRoomDim + rnd.nextInt(maxDim - roomOptions.minRoomDim + 1)

}
