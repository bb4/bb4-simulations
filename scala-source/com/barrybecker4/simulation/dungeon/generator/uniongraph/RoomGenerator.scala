package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.bsp.room.RoomGenerator.RND
import com.barrybecker4.simulation.dungeon.model.{Decoration, DungeonOptions, Room}
import com.barrybecker4.simulation.dungeon.generator.uniongraph.BoxDecomposer

import java.awt.Color
import scala.collection.mutable
import scala.util.Random


object RoomGenerator {
  private val RND: Random = Random(0)
}

case class RoomGenerator(options: DungeonOptions, rnd: Random = RND){

  private val queue: mutable.PriorityQueue[Box] = mutable.PriorityQueue()(Ordering.by(_.getArea))
  private val boxDecomposer: BoxDecomposer = BoxDecomposer(options)
  private val minDim = options.getMinPaddedDim

  def generateRooms(): Set[Room] = {

    val initialBox = Box(0, 0, options.dimension.height, options.dimension.width)
    queue.enqueue(initialBox)
    var rooms: Set[Room] = Set()
    val totalArea = initialBox.getArea
    var usedArea = 0

    while (queue.nonEmpty && (100 * usedArea.toFloat / totalArea) < options.percentFilled) {
      val box = queue.dequeue()
      if (box.getWidth > minDim && box.getHeight > minDim) {
        val (room, boxes) = boxDecomposer.createRoomInBox(box)
        rooms += room
        usedArea += room.box.getArea
        queue ++= boxes
      }
    }

    rooms
  }
}
