// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonMap, Orientation, Path, Room}
import com.barrybecker4.simulation.dungeon.model.Orientation.*

import java.awt.Dimension
import scala.collection.immutable.HashSet
import scala.util.Random


object RoomJoiner {
  private val RND = Random(0)
}

case class RoomJoiner(connectivity: Float, dungeonDim: Dimension, rnd: Random = RND) {

  private var dMap: DungeonMap = _
  private var roomToRoomSet: RoomToRoomSetMap = _
  private val startPointGenerator = StartPointGenerator(rnd)

  /**
   * Send corridor rays out from the specified room and join with other nearby rooms or intersecting corridors.
   * Add any corridor that joins with a room or corridor in another roomSet.
   */
  def doJoin(room: Room, dungeonMap: DungeonMap,
             roomToRoomSetMap: RoomToRoomSetMap): (DungeonMap, RoomToRoomSetMap) = {

    dMap = dungeonMap
    roomToRoomSet = roomToRoomSetMap

    doJoinForHorizontalSides(room)
    doJoinForVerticalSides(room)

    (dMap, roomToRoomSet)
  }

  private def doJoinForHorizontalSides(room: Room): Unit = {
    val topLeft = room.box.getTopLeftCorner
    val bottomRight = room.box.getBottomRightCorner

    val startingPoints = startPointGenerator.getStartingPoints(topLeft.getY, bottomRight.getY)

    for (direction <- Seq(1, -1)) {
      var done = false
      var remainingStartingPoints = startingPoints
      val startXPos = if (direction == 1) bottomRight.getX else topLeft.getX

      // march to right or left
      while (!done && remainingStartingPoints.nonEmpty) {
        val startYPos = remainingStartingPoints.head
        remainingStartingPoints = remainingStartingPoints.tail
        done = processForStartingPoint(IntLocation(startYPos, startXPos), room, direction, 0)
      }
    }
  }

  private def doJoinForVerticalSides(room: Room): Unit = {
    val topLeft = room.box.getTopLeftCorner
    val bottomRight = room.box.getBottomRightCorner

    val startingPoints = startPointGenerator.getStartingPoints(topLeft.getX, bottomRight.getX)

    for (direction <- Seq(1, -1)) {
      var done = false
      var remainingStartingPoints = startingPoints
      val startYPos = if (direction == 1) bottomRight.getY else topLeft.getY

      // march to bottom or top
      while (!done && remainingStartingPoints.nonEmpty) {
        val startXPos = remainingStartingPoints.head
        remainingStartingPoints = remainingStartingPoints.tail
        done = processForStartingPoint(IntLocation(startYPos, startXPos), room, 0, direction)
      }
    }
  }

  /**
   * @return true if a connection was made
   */
  private def processForStartingPoint(startLocation: IntLocation, room: Room,
                                      xDirection: Int, yDirection: Int): Boolean = {
    val newConnection: Option[(Corridor, Room | Corridor)] =
      checkForHit(startLocation, room, xDirection, yDirection)
    if (newConnection.nonEmpty) {
      update(room, newConnection.get)
      true
    } else false
  }

  /** @return the intersected object if anything */
  private def checkForHit(startPos: IntLocation, room: Room,
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

      if (dMap(pos2).isDefined) {
        val middleItem = dMap(pos2).get
        val path = getPath(xDirection, yDirection, startPos, pos2)
        middleItem match {
          case c: Corridor =>
            val rSet = roomToRoomSet(room)
            return Some((Corridor(path, HashSet(room)), middleItem))
          case _ => if (dMap.isRoom(pos1) || dMap.isRoom(pos3))
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
    (dMap.isRoom(pos1) || dMap.isRoom(pos3)) && !dMap.isRoom(pos2)

  private def isOverExistingCorridor(pos1: IntLocation, pos2: IntLocation, pos3: IntLocation): Boolean =
    dMap.isCorridor(pos1) || dMap.isCorridor(pos2) || dMap.isCorridor(pos3)

  /**
   * Update dungeonMap and roomsToRoomSetMap given the new connection.
   * @param connection corridor that reaches another room or corridor
   * @return true if connected with another set
   */
  private def update(room: Room, connection: (Corridor, Room | Corridor)): Boolean = {

    var thisRoomSet = roomToRoomSet(room)
    var otherRoomSet = getOtherRoomsSet(connection._2)
    val connectsOtherRoomSet = thisRoomSet != otherRoomSet

    // only add connection beyond what is minimally needed if connectivity requires it
    if (!connectsOtherRoomSet && rnd.nextFloat() > connectivity)
      return false

    val corridorToAdd =
      if (connection._2.isInstanceOf[Room]) connection._1
      else {
        val corridor = connection._2.asInstanceOf[Corridor]
        // first remove the corridor from the roomSet to which it belongs
        if (connectsOtherRoomSet)
          otherRoomSet = otherRoomSet.removeCorridor(corridor)
        else
          thisRoomSet = thisRoomSet.removeCorridor(corridor)
        corridor.addCorridor(connection._1)
      }

    if (isAlreadyDirectlyConnected(corridorToAdd, thisRoomSet))
      return false

    val baseRoomSet =
      if (connectsOtherRoomSet) thisRoomSet.mergeRoomSet(otherRoomSet)
      else thisRoomSet
    val updatedRoomSet = baseRoomSet.addCorridor(corridorToAdd)

    assert(updatedRoomSet.rooms.size > 1)
    roomToRoomSet = roomToRoomSet.update(updatedRoomSet)
    dMap = dMap.addCorridors(updatedRoomSet.corridors)
    connectsOtherRoomSet
  }

  private def getOtherRoomsSet(thingConnected: Room | Corridor): RoomSet =
    thingConnected match {
      case room: Room => roomToRoomSet(room)
      case corridor: Corridor => roomToRoomSet(corridor.rooms.head)
    }

  // avoid multiple corridors to same room by checking that we don't already have a direct connection
  private def isAlreadyDirectlyConnected(corridor: Corridor, roomSet: RoomSet): Boolean =
    roomSet.corridors.exists(c => c.rooms.intersect(corridor.rooms).size == 2)
}
