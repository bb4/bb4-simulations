package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.organic.room.RoomGenerator.RND
import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.BoxDecomposer
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions
import com.barrybecker4.simulation.dungeon.model.{DungeonMap, Room}

import scala.collection.mutable
import scala.util.Random


object RoomGenerator {
  private val RND: Random = Random(0)
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND) {

  private val queue: mutable.PriorityQueue[Box] = mutable.PriorityQueue()(Ordering.by(_.getArea))
  private val boxDecomposer: BoxDecomposer = BoxDecomposer(options, rnd)
  private val roomOptions = options.roomOptions
  private val minDim = roomOptions.getMinPaddedDim

  def generateRooms(): DungeonMap = {

    val initialBox = Box(1, 1, options.dimension.height, options.dimension.width)
    queue.enqueue(initialBox)
    var rooms: Set[Room] = Set()

    // about 10% will never be used because of space between rooms
    val availableArea = 0.8 * initialBox.getArea
    var usedArea = 0

    while (queue.nonEmpty && (100 * usedArea.toFloat / availableArea) < roomOptions.percentFilled) {
      val box = queue.dequeue()
      if (box.getWidth > minDim && box.getHeight > minDim) {
        val (room, boxes) = boxDecomposer.createRoomInBox(box)
        rooms += room
        usedArea += room.box.getArea
        queue ++= boxes
      }
    }

    new DungeonMap(rooms)
  }
}
