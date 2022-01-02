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
   * Finally we may need to handle the case where there were no intersections with another roomSet.
   * @return the updated dungeonMap and roomToRoomSetMap
   */
  def doJoin(room: Room, dungeonMap: DungeonMap,
             roomToRoomSetMap: RoomToRoomSetMap): (DungeonMap, RoomToRoomSetMap) = {

    dMap = dungeonMap
    roomToRoomSet = roomToRoomSetMap

    val origCorridors = dMap.getCorridors
    println("numCorridors before doJoin: " + origCorridors.size)

    val hConnectionsForRoom = doJoinForHorizontalSides(room)
    val vConnectionsForRoom = doJoinForVerticalSides(room)

    //println("connectionsForRoom h=" + hConnectionsForRoom + " v=" + vConnectionsForRoom)
    val newCorridors = dMap.getCorridors
    println("numCorridors after doJoin: " + newCorridors.size)
    //if (newCorridors.size - origCorridors.size != hConnectionsForRoom + vConnectionsForRoom)
    //  throw new IllegalStateException()

    (dMap, roomToRoomSet)
  }

  private def doJoinForHorizontalSides(room: Room): Int = {
    val topLeft = room.box.getTopLeftCorner
    val bottomRight = room.box.getBottomRightCorner

    val startingPoints = getStartingPoints(topLeft.getY, bottomRight.getY)
    var numConnections = 0

    for (direction <- Seq(1, -1)) {
      var done = false
      var remainingStartingPoints = startingPoints
      val startXPos = if (direction == 1) bottomRight.getX else topLeft.getX

      // march to right or left
      while (!done && remainingStartingPoints.nonEmpty) {
        val startYPos = remainingStartingPoints.head
        remainingStartingPoints = remainingStartingPoints.tail
        val newConnection: Option[(Corridor, Room | Corridor)] =
          checkForHit(IntLocation(startYPos, startXPos), room, direction, 0)
        if (newConnection.nonEmpty) {
          numConnections += 1
          done = update(room, newConnection.get)
        }
      }
    }
    numConnections
  }

  private def doJoinForVerticalSides(room: Room): Int = {
    val topLeft = room.box.getTopLeftCorner
    val bottomRight = room.box.getBottomRightCorner

    val startingPoints = getStartingPoints(topLeft.getX, bottomRight.getX)
    var numConnections = 0;

    for (direction <- Seq(1, -1)) {
      var done = false
      var remainingStartingPoints = startingPoints
      val startYPos = if (direction == 1) bottomRight.getY else topLeft.getY

      // march to bottom or top
      while (!done && remainingStartingPoints.nonEmpty) {
        val startXPos = remainingStartingPoints.head
        remainingStartingPoints = remainingStartingPoints.tail
        val newConnection: Option[(Corridor, Room | Corridor)] =
          checkForHit(IntLocation(startYPos, startXPos), room, 0, direction)
        if (newConnection.nonEmpty) {
          numConnections += 1
          done = update(room, newConnection.get)
        }
      }
    }
    numConnections
  }

  /**
   * When a ray with width 3 is sent out, the cases are
   * 0. If 1st or third points intersect a room, but not the second, then abort this ray and try next
   * 1. If we reach a dungeon edge, abort and try next.
   * 2. the middle cell intersect a corridor
   * 3. If at least 2 of the 3 cells intersect a room, then create a corridor connection
   * 4. If nothing intersects, abort and try next
   * @return the intersected object if anything
   */
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

      if (firstPosition && isOverExistingCorridor(pos1, pos2, pos3)) {
        println("firstPosition "+ pos2 + " was over existing corridor. From room " + room)
        return None
      }
      firstPosition = false

      if (dMap(pos2).isDefined) {
        val middleItem = dMap(pos2).get
        val path = getPath(xDirection, yDirection, startPos, pos2)
        if (middleItem.isInstanceOf[Corridor]) {
          /*
          val c = middleItem.asInstanceOf[Corridor]
          println(path.toString() + " hit another corridor\n")
          println("The corridor that was hit was "+ middleItem + "\n")
          println("And it came from room:" + room + " belonging to roomSet.corridors:")
          val rSet = roomToRoomSet(room)
          if (rSet.corridors.contains(c)) {
            val cs = rSet.corridors
            println("room (" + cs.size + ")\n" + cs.mkString("\n"))
          }
          else {
            val cs = roomToRoomSet(c.rooms.head).corridors
            println("other (" + cs.size + ") \n" + cs.mkString("\n"))
          }
          return Some((Corridor(path, HashSet(room)), middleItem))
          */
          return None
        }
        else if (dMap.isRoom(pos1) || dMap.isRoom(pos3)) {
          println("found connection from " + startPos + " middleItem=" + middleItem)
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
                      startPos: IntLocation, pos2: IntLocation): Seq[(IntLocation, IntLocation)] = {
    val firstPos =
      if (xDirection == 0) IntLocation(startPos.getY, pos2.getX)
      else IntLocation(pos2.getY, startPos.getX)
    val secondPos = IntLocation(pos2.getY + (if (yDirection == -1) 1 else 0), pos2.getX + (if (xDirection == -1) 1 else 0))
    Seq((firstPos, secondPos))
  }

  private def grazesRoom(pos1: IntLocation, pos2: IntLocation, pos3: IntLocation): Boolean =
    (dMap.isRoom(pos1) || dMap.isRoom(pos3)) && !dMap.isRoom(pos2)

  private def isOverExistingCorridor(pos1: IntLocation, pos2: IntLocation, pos3: IntLocation): Boolean =
    dMap.isCorridor(pos1) || dMap.isCorridor(pos2) || dMap.isCorridor(pos3)

  /**
   * Update dungeonMap and roomsToRoomSetMap given the new connection.
   *
   *   If connection is to a Room:
   *   - if the rooms are from different roomSets, merge them and add the corridor to the merged roomSet
   *   - if same roomSet, then just add the corridor
   *
   *   If connection is to a Corridor:
   *   - add the segment to the corridor
   *   - replace the corridor in the roomSet that it belongs to
   *   - if roomSets are different, merge the roomSets:
   *       the one that we came from and the one that the corridor belongs to
   *
   * @param connection corridor that reaches another room or corridor
   * @return true if connected with another set
   */
  private def update(room: Room, connection: (Corridor, Room | Corridor)): Boolean = {

    var thisRoomSet = roomToRoomSet(room)
    var otherRoomSet = connection._2 match {
      case room1: Room => roomToRoomSet(room1)
      case _ => roomToRoomSet(connection._2.asInstanceOf[Corridor].rooms.head)
    }
    val connectsOtherRoomSet = thisRoomSet != otherRoomSet

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

    val baseRoomSet =
      if (connectsOtherRoomSet) {
        // verify that we are merging disjoint roomSet
        assert(thisRoomSet.rooms.intersect(otherRoomSet.rooms).isEmpty)
        thisRoomSet.mergeRoomSet(otherRoomSet)
      }
      else thisRoomSet
    val updatedRoomSet = baseRoomSet.addCorridor(corridorToAdd)

    assert(updatedRoomSet.rooms.size > 1)
    roomToRoomSet = roomToRoomSet.update(updatedRoomSet)
    dMap = dMap.addCorridors(updatedRoomSet.corridors)
    connectsOtherRoomSet
  }

  private def getStartingPoints(startPos: Int, stopPos: Int): List[Int] = {
    var startingPoints: List[Int] = List()

    for (i <- startPos to stopPos - RAY_WIDTH by RAY_WIDTH) {
      startingPoints :+= i
    }
    rnd.shuffle(startingPoints)
  }
}
