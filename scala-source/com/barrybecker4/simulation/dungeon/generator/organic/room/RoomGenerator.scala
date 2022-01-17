// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.organic.room.RoomGenerator.RND
import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.{RoomAndCorridor, SproutLocation, SproutLocationFinder}
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions
import com.barrybecker4.simulation.dungeon.model.util.Intersector
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonMap, Room}
import com.barrybecker4.simulation.dungeon.model.Orientation.*

import scala.collection.immutable.Queue
import scala.collection.mutable
import scala.util.Random


object RoomGenerator {
  private val RND: Random = Random(0)
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private var dungeonMap: DungeonMap = _
  private var queue: Queue[SproutLocation] = Queue()
  private val roomOptions = options.roomOptions
  private val minPaddedDim = roomOptions.getMinPaddedDim
  private val connectivity = options.connectivity


  def generateRooms(): DungeonMap = {

    val bounds = Box(1, 1, options.dimension.height, options.dimension.width)

    val unusedSprouts = generateRoomsOrganically(bounds)
    optionallyAddMoreCorridors(unusedSprouts, bounds)
    dungeonMap
  }

  private def generateRoomsOrganically(bounds: Box): List[SproutLocation] = {

    val randomRoomCreator = RandomRoomCreator(roomOptions, bounds, rnd)
    val sproutFinder = SproutLocationFinder(bounds, rnd)
    var unusedSprouts: List[SproutLocation] = List()

    val initialRoom = randomRoomCreator.createRoom()
    dungeonMap = new DungeonMap(Set(initialRoom))
    val sproutLocations: Set[SproutLocation] = sproutFinder.findLocations(initialRoom)
    queue = queue.enqueueAll(sproutLocations)

    while (queue.nonEmpty) {
      val result = queue.dequeue
      val sproutLocation = result._1
      queue = result._2

      val possibleRoomAndCorridor: Option[RoomAndCorridor] =
        randomRoomCreator.createRoomFromSproutLocation(sproutLocation, dungeonMap)

      if (possibleRoomAndCorridor.isDefined) {
        updateMap(possibleRoomAndCorridor.get)
        val room = possibleRoomAndCorridor.get.room
        queue = queue.enqueueAll(sproutFinder.findLocations(room))
      }
      else unusedSprouts :+= sproutLocation
    }

    unusedSprouts
  }

  private def updateMap(roomAndCorridor: RoomAndCorridor): Unit = {
    dungeonMap = dungeonMap.addRoom(roomAndCorridor.room)
    dungeonMap = dungeonMap.addCorridor(roomAndCorridor.corridor)
  }

  /** For each unused sprout location, consider adding a corridor depending on connectivity param
    */
  private def optionallyAddMoreCorridors(unusedSprouts: List[SproutLocation], bounds: Box): DungeonMap = {

    if (connectivity < 0.05)
      return dungeonMap

    for (sprout <- unusedSprouts)
      considerAddingCorridor(sprout)

    dungeonMap
  }

  private def considerAddingCorridor(sprout: SproutLocation): Unit = {
    val xDirection = if (sprout.orientation == Horizontal) sprout.direction else 0
    val yDirection = if (sprout.orientation == Vertical) sprout.direction else 0

    val pos = sprout.position
    val xOffset = if (xDirection == -1) -1 else 0
    val yOffset = if (yDirection == -1) -1 else 0
    val startPosition = IntLocation(pos.getY - yOffset, pos.getX - xOffset)

    val newConnection: Option[(Corridor, Room | Corridor)] =
      Intersector(options.dimension, dungeonMap).checkForIntersection(startPosition, sprout.room, xDirection, yDirection)
    if (newConnection.nonEmpty)
      update(sprout.room, newConnection.get)
  }

  /** Update dungeonMap and roomsToRoomSetMap given the new connection.
    * @param connection corridor that reaches another room or corridor
    * @return true if connected with another set
    */
  private def update(room: Room, connection: (Corridor, Room | Corridor)): Unit = {

    if (alreadyConnected(room, connection) || Math.random() > connectivity)
      return

    val corridorToAdd =
      if (connection._2.isInstanceOf[Room]) connection._1
      else {
        val corridor = connection._2.asInstanceOf[Corridor]
        corridor.addCorridor(connection._1)
      }

    dungeonMap = dungeonMap.addCorridor(corridorToAdd)
  }

  private def alreadyConnected(room: Room, connection: (Corridor, Room | Corridor)): Boolean = {
    if (connection._2.isInstanceOf[Room]) dungeonMap.isConnected(room, connection._2.asInstanceOf[Room])
    else false
  }

}
