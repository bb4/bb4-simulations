// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model.util

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.Orientation.{Horizontal, Vertical}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonMap, Path, Room}

import scala.collection.immutable.HashSet
import java.awt.Dimension

/**
  * Use to check for connections from rooms 
  */
case class Intersector(dungeonDim: Dimension, dungeonMap: DungeonMap) {

  /** Send out a ray from the specified point on the edge of a room and see what it hits.
    * It will hit either nothing, room, or corridor.
    * @return noting, or the corridor to the intersected object (and that object Room|Corridor)
    */
  def checkForIntersection(startPos: IntLocation, room: Room,
    xDirection: Int, yDirection: Int): Option[(Corridor, Room | Corridor)] = {
    var xPos = startPos.getX
    var yPos = startPos.getY
    var firstPosition = true

    while (true) {
      xPos += xDirection
      yPos += yDirection
      if (xPos <= 0 || xPos >= dungeonDim.width || yPos <= 0 || yPos >= dungeonDim.height)
        return None
      val (pos1, pos2, pos3) = getStartTriple(xDirection, xPos, yPos, startPos)

      if (grazesRoom(pos1, pos2, pos3))
        return None

      if (firstPosition && isOverExistingCorridor(pos1, pos2, pos3))
        return None
      firstPosition = false

      if (dungeonMap(pos2).isDefined) {
        val middleItem = dungeonMap(pos2).get
        val path = getPath(xDirection, yDirection, startPos, pos2)
        middleItem match {
          case c: Corridor =>
            return Some((Corridor(path, HashSet(room)), middleItem))
          case _ => if (dungeonMap.isRoom(pos1) || dungeonMap.isRoom(pos3))
            return Some((Corridor(path, HashSet(room, middleItem.asInstanceOf[Room])), middleItem))
        }
      }
    }
    None
  }

  private def getStartTriple(xDirection: Int, xPos: Int, yPos: Int,
    startPos: IntLocation): (IntLocation, IntLocation, IntLocation) = {
    if (xDirection == 0)
      (IntLocation(yPos, startPos.getX), IntLocation(yPos, startPos.getX + 1), IntLocation(yPos, startPos.getX + 2))
    else
      (IntLocation(startPos.getY, xPos), IntLocation(startPos.getY + 1, xPos), IntLocation(startPos.getY + 2, xPos))
  }

  private def getPath(xDirection: Int, yDirection: Int,
    startPos: IntLocation, pos2: IntLocation): Seq[Path] = {
    val firstPos =
      if (xDirection == 0) IntLocation(Math.min(startPos.getY, pos2.getY) + (if (yDirection == -1) 1 else 0), pos2.getX)
      else IntLocation(pos2.getY, Math.min(startPos.getX, pos2.getX) + (if (xDirection == -1) 1 else 0))

    val secondPos = IntLocation(
      Math.max(startPos.getY, pos2.getY),
      Math.max(startPos.getX, pos2.getX)
    )

    val orientation = if (xDirection == 0) Vertical else Horizontal
    val length =
      if (orientation == Vertical) secondPos.getY - firstPos.getY
      else secondPos.getX - firstPos.getX

    Seq(Path(firstPos, orientation, length))
  }

  private def grazesRoom(pos1: IntLocation, pos2: IntLocation, pos3: IntLocation): Boolean =
    (dungeonMap.isRoom(pos1) || dungeonMap.isRoom(pos3)) && !dungeonMap.isRoom(pos2)

  private def isOverExistingCorridor(pos1: IntLocation, pos2: IntLocation, pos3: IntLocation): Boolean =
    dungeonMap.isCorridor(pos1) || dungeonMap.isCorridor(pos2) || dungeonMap.isCorridor(pos3)

}
