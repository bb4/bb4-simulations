package com.barrybecker4.simulation.dungeon.generator.uniongraph

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.bsp.room.RoomGenerator.RND
import com.barrybecker4.simulation.dungeon.model.{DungeonOptions, Room, Decoration}

import java.awt.Color
import scala.collection.mutable
import scala.util.Random


object RoomGenerator {
  private val ROOM_DECORATION: Decoration =
    Decoration(new Color(100, 0, 230), new Color(230, 190, 255))
  private val RND: Random = Random(0)
}

case class RoomGenerator (options: DungeonOptions, rnd: Random = RND){

  private var queue: mutable.PriorityQueue[Box] = mutable.PriorityQueue()(Ordering.by(_.getArea))

  def generateRooms(): Set[Room] = {
    val dim = options.dimension
    Set()
  }
}
