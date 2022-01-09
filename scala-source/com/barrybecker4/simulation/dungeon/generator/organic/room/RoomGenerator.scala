// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.organic.room.RoomGenerator.RND
import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.{RoomAndCorridor, SproutLocation, SproutLocationFinder}
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, Room}

import scala.collection.immutable.Queue
import scala.collection.mutable
import scala.util.Random


object RoomGenerator {
  private val RND: Random = Random(0)
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private var queue: Queue[SproutLocation] = Queue()
  private val roomOptions = options.roomOptions
  private val minPaddedDim = roomOptions.getMinPaddedDim


  def generateRooms(): DungeonMap = {

    val bounds = Box(1, 1, options.dimension.height, options.dimension.width)
    val sproutFinder = SproutLocationFinder(bounds, rnd)
    val randomRoomCreator = RandomRoomCreator(roomOptions, bounds, rnd)
    val initialRoom = randomRoomCreator.createRoom()

    val sproutLocations: Set[SproutLocation] = sproutFinder.findLocations(initialRoom)
    queue = queue.enqueueAll(sproutLocations)
    var dungeonMap = new DungeonMap(Set(initialRoom))

    while (queue.nonEmpty) {
      val result = queue.dequeue
      val sproutLocation = result._1
      queue = result._2

      val possibleRoomAndCorridor: Option[RoomAndCorridor] =
        randomRoomCreator.createRoomFromSproutLocation(sproutLocation, dungeonMap)

      if (possibleRoomAndCorridor.isDefined) {
        val room = possibleRoomAndCorridor.get.room
        val corridor = possibleRoomAndCorridor.get.corridor
        dungeonMap = dungeonMap.addRoom(room)
        dungeonMap = dungeonMap.addCorridor(corridor)

        queue = queue.enqueueAll(sproutFinder.findLocations(room))
      }
    }
    dungeonMap
  }

}
