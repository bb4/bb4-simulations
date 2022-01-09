package com.barrybecker4.simulation.dungeon.generator.organic.room.sprout

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.{SproutLocation, SproutPointGenerator}
import com.barrybecker4.simulation.dungeon.model.Orientation.*
import com.barrybecker4.simulation.dungeon.model.Room

import scala.util.Random


object SproutLocationFinder {
  private val RND = Random(0)
}

case class SproutLocationFinder(bounds: Box, rnd: Random = RND) {

  private val sproutPointGenerator = SproutPointGenerator(rnd)

  /**
   * @return possible locations for sprouting rooms
   *         around the perimeter of the specified room.
   */
  def findLocations(room: Room): Set[SproutLocation] = {
    val horizontalSprouts = findHorizontalSproutLocations(room)
    val verticalSprouts = findVerticalSproutLocations(room)
    horizontalSprouts ++ verticalSprouts
  }

  private def findHorizontalSproutLocations(room: Room): Set[SproutLocation] = {
    val box = room.box
    val startYPos = box.getTopLeftCorner.getY 
    val stopYPos = box.getBottomRightCorner.getY
    val verticalPoints = sproutPointGenerator.getSproutPoints(startYPos, stopYPos)

    var sprouts: Set[SproutLocation] = Set()
    for (yPos <- verticalPoints) {
      val leftLoc = IntLocation(yPos, box.getTopLeftCorner.getX - 1)
      sprouts += SproutLocation(room, leftLoc, Horizontal, -1)
      val rightLoc = IntLocation(yPos, box.getBottomRightCorner.getX)
      sprouts += SproutLocation(room, rightLoc, Horizontal, 1)
    }
    sprouts
  }

  private def findVerticalSproutLocations(room: Room): Set[SproutLocation] = {
    val box = room.box
    val startXPos = box.getTopLeftCorner.getX
    val stopXPos = box.getBottomRightCorner.getX
    val horizontalPoints = sproutPointGenerator.getSproutPoints(startXPos, stopXPos)

    var sprouts: Set[SproutLocation] = Set()
    for (xPos <- horizontalPoints) {
      val topLoc = IntLocation(box.getTopLeftCorner.getY -1, xPos)
      sprouts += SproutLocation(room, topLoc, Vertical, -1)
      val bottomLoc = IntLocation(box.getBottomRightCorner.getY, xPos)
      sprouts += SproutLocation(room, bottomLoc, Vertical, 1)
    }
    sprouts
  }
}
