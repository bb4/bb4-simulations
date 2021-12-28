package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Room}

import scala.util.Random
import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.uniongraph.BoxDecomposer.RND

import scala.collection.immutable.HashSet


object BoxDecomposer {
  private val RND: Random = Random(0)
}

case class BoxDecomposer(options: DungeonOptions, rnd: Random = RND) {

  private val roomPadding = options.roomPadding
  private val padding = if (options.halfPadded) roomPadding else 2 * roomPadding
  private val frontPadding = if (options.halfPadded) 0 else roomPadding
  private val RATIO = options.getMaxPaddedWidth / options.getMaxPaddedHeight
  private val maxPaddedWidth = options.getMaxPaddedWidth
  private val maxPaddedHeight = options.getMaxPaddedHeight

  /**
   * If half-padded, then the padding only goes at the bottom
   * @param box the box to find a room in
   * @return the room and a set of boxes that repesentt the area outside the box
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
    val room: Room = Room(roomBox, Set())
    assert(roomContainer.contains(roomBox.getTopLeftCorner) && roomContainer.contains(roomBox.getBottomRightCorner))

    println("\n----inner = " + roomContainer)
    val areas = findUnoccupiedAreas(box, roomContainer)
    (room, areas)
  }

  private def findUnoccupiedAreas(box: Box, innerContainer: Box): Set[Box] = {
    println("box = " + box)
    assert(box.contains(innerContainer.getTopLeftCorner) && box.contains(innerContainer.getBottomRightCorner))

    if (box == innerContainer)
      return HashSet[Box]()

    val divideHorizontally = box.getHeight == innerContainer.getHeight
      || ((box.getWidth - innerContainer.getWidth).toFloat / (box.getHeight - innerContainer.getHeight) > RATIO)
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
      println(s"leftWider=$leftWider newBox=${newBox.getArea} newWidth=${newBox.getWidth}")

      assert(newBox.contains(innerContainer.getTopLeftCorner) && newBox.contains(innerContainer.getBottomRightCorner))
      findUnoccupiedAreas(newBox, innerContainer) + shavedBox
    } else {
      assert(box.getHeight > innerContainer.getHeight, s"inner ht (${innerContainer.getHeight}) was larger than ht (${box.getHeight})")
      val topTaller = innerTopLeft.getY - boxTopLeft.getY > boxBottomRight.getY - innerBottomRight.getY
      val shavedBox = if (topTaller) new Box(boxTopLeft, IntLocation(innerTopLeft.getY, boxBottomRight.getX))
                      else new Box(IntLocation(innerBottomRight.getY, boxTopLeft.getX), boxBottomRight)
      val newBox = if (topTaller) new Box(IntLocation(innerTopLeft.getY, boxTopLeft.getX), boxBottomRight)
                   else new Box(boxTopLeft, IntLocation(innerBottomRight.getY, boxBottomRight.getX))
      println(s"topTaller=$topTaller newBox=${newBox.getArea} newht=${newBox.getHeight}")
      assert(newBox.getHeight > 0, "newBox="+ newBox)
      assert(box.getHeight > innerContainer.getHeight, s"inner ht (${innerContainer.getHeight}) was larger than ht (${box.getHeight})")

      assert(newBox.contains(innerContainer.getTopLeftCorner) && newBox.contains(innerContainer.getBottomRightCorner))
      findUnoccupiedAreas(newBox, innerContainer) + shavedBox
    }
  }

  private def randomWidth(box: Box): Int =
    if (box.getWidth < maxPaddedWidth) box.getWidth - padding
    else Math.min(box.getWidth - padding, randomDim(options.maxRoomWidth))

  private def randomHeight(box: Box): Int =
    if (box.getHeight < maxPaddedHeight) box.getHeight - padding
    else Math.min(box.getHeight - padding, randomDim(options.maxRoomHeight))

  private def randomDim(maxDim: Int): Int = options.minRoomDim + rnd.nextInt(maxDim - options.minRoomDim)
}
