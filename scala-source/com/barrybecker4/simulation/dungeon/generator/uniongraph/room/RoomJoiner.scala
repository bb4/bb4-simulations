package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.RoomJoiner.RAY_WIDTH
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonMap, Room}

import java.awt.Dimension
import scala.collection.immutable.HashSet
import scala.util.Random


object RoomJoiner {
  private val RAY_WIDTH = 3
  private val RND = Random(0)
}

case class RoomJoiner(connectivity: Float, dungeonDim: Dimension, rnd: Random = RND) {

  private var dMap: DungeonMap = _
  private var roomToRoomSet: RoomToRoomSetMap = _

  /**
   * Send corridor rays out from the specified room and join with other nearby rooms or intersecting corridors.
   * Add any corridor that joins with a room or corridor in another roomSet.
   * For other hits, add if rnd < connectivity.
   * If none exist, just add the corridor to the nearest object.
   *
   * For each side, create set pf possible ray exit points and add to a set.
   * Try all the rays to see if we can reach something in a different set.
   *  If in different set, merge the 2 sets.
   *  If in same set and not already directly connected, add it if rnd < connectivityThresh
   *
   * Finally we may need to handle the case where there were no intersections with another roomset.
   *
   * @return the updated dungeonMap and roomToRoomSetMap
   */
  def doJoin(room: Room, dungeonMap: DungeonMap,
             roomToRoomSetMap: RoomToRoomSetMap): (DungeonMap, RoomToRoomSetMap) = {

    dMap = dungeonMap
    roomToRoomSet = roomToRoomSetMap
    doJoinForHorizontalSides(room)
    doJoinForVerticalSides(room)
    doJoinForVerticalSides(room)
    (dMap, roomToRoomSet)
  }

  private def doJoinForHorizontalSides(room: Room): Unit = {

    val topLeft = room.box.getTopLeftCorner
    val bottomRight = room.box.getBottomRightCorner

    val startingPoints = getStartingPoints(topLeft.getY, bottomRight.getY)

    for (direction <- Seq(1, -1)) {
      var done = false
      var remainingStartingPoints = startingPoints
      val startXPos = if (direction == 1) bottomRight.getX else topLeft.getX

      // march to right or left
      while (!done && remainingStartingPoints.nonEmpty) {
        val startYPos = remainingStartingPoints.head
        remainingStartingPoints = remainingStartingPoints.tail
        val thing: Option[(Corridor, Room | Corridor)] =
          checkForHit(IntLocation(startYPos, startXPos), room, direction, 0)
        if (thing.nonEmpty) {
          // if think is in other roomSet, definitely added it, otherwise maybe add it.
          update(thing.get)
          done = true // if reached something in another set
        }
      }
    }
  }

  private def doJoinForVerticalSides(room: Room): Unit = {
    val topLeft = room.box.getTopLeftCorner
    val bottomRight = room.box.getBottomRightCorner

    val startingPoints = getStartingPoints(topLeft.getX, bottomRight.getX)

    for (direction <- Seq(1, -1)) {
      var done = false
      var remainingStartingPoints = startingPoints
      val startYPos = if (direction == 1) bottomRight.getY else topLeft.getY

      // march to bottom or top
      while (!done && remainingStartingPoints.nonEmpty) {
        val startXPos = remainingStartingPoints.head
        remainingStartingPoints = remainingStartingPoints.tail
        val thing: Option[(Corridor, Room | Corridor)] =
          checkForHit(IntLocation(startYPos, startXPos), room, 0, direction)
        if (thing.nonEmpty) {
          // if think is in other roomSet, definitely added it, otherwise maybe add it.
          done = update(thing.get)
        }
      }
    }
  }

  /**
   *  When a ray with width 3 is sent out, the cases are
   * 0. If 1st or third points intersect a room, but not the second, then abort this ray and try next
   * 1. If we reach a dungeon edge, abort and try next.
   * 2. If the middle cell intersect a corridor -
   *   - add the segment to the corridor
   *   - replace the corridor in the roomSet that it belongs to
   *   - if roomSets are different, merge the roomSets
   *      - the one that we came from and the one that the corridor belongs to
   * 2.If at least 2 of the 3 cells intersect a room, then create a corridor connection
   *   - if the rooms are from different roomSets, merge them and add the corridor to the merged roomSet
   *   - if same roomSet, then just add the corridor
   * 3. If nothing intersect, abort and try next
   * @return the intersected object if anything
   */
  private def checkForHit(startPos: IntLocation, room: Room,
                          xDirection: Int, yDirection: Int): Option[(Corridor, Room | Corridor)] = {
    var xPos = startPos.getX
    var yPos = startPos.getY

    while (true) {
      xPos += xDirection
      yPos += yDirection
      if (xPos == 0 || xPos == dungeonDim.width || yPos == 0 || yPos == dungeonDim.height) {
        return None
      }
      val pos1 = IntLocation(startPos.getY, xPos)
      val pos2 = if (xDirection == 0) IntLocation(yPos, startPos.getX + 1) else  IntLocation(startPos.getY + 1, xPos)
      val pos3 = if (xDirection == 0) IntLocation(yPos, startPos.getX + 2) else  IntLocation(startPos.getY + 2, xPos)

      if (grazesRoom(pos1, pos2, pos3)) {
        return None
      }
      val middleItem = dMap(pos2)
      if (middleItem.isInstanceOf[Corridor]) {
        val path = getPath(xDirection, yDirection, startPos, pos2)
        return Some((Corridor(path, HashSet(room)), middleItem))
      }
      if (dMap(pos1).isInstanceOf[Room] || dMap(pos3).isInstanceOf[Room]) {
        val path = getPath(xDirection, yDirection, startPos, pos2)
        return Some((Corridor(path, HashSet(room, middleItem.asInstanceOf[Room])), middleItem))
      }
    }
    None
  }

  private def getPath(xDirection: Int, yDirection: Int,
                      startPos: IntLocation, pos2: IntLocation): Seq[(IntLocation, IntLocation)] = {
    val firstPos =
      if (xDirection == 0) IntLocation(startPos.getY + yDirection, pos2.getX)
      else IntLocation(pos2.getY, startPos.getX + xDirection)
    Seq((firstPos, pos2))
  }


  private def grazesRoom(pos1: IntLocation, pos2: IntLocation, pos3: IntLocation): Boolean =
    (dMap(pos1).isInstanceOf[Room] || dMap(pos3).isInstanceOf[Room]) && !dMap(pos2).isInstanceOf[Room]

  /**
   * Update dungeonMap and roomsToRoomSetMap given the new connection
   * @param thing corridor that reaches another room or corridor
   * @return true if coonected with another set
   */
  private def update(thing: (Corridor, Room | Corridor)): Boolean = {
    true
  }

  private def getStartingPoints(startPos: Int, stopPos: Int): List[Int] = {
    var startingPoints: List[Int] = List()

    for (i <- stopPos + 1 until stopPos by RAY_WIDTH) {
      startingPoints :+= i
    }
    rnd.shuffle(startingPoints)
  }
}
