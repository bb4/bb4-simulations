// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.options.{DungeonOptions, RoomOptions}
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Dimension
import scala.util.Random

class BoxDecomposerSuite extends AnyFunSuite {

  test("createRoomInBox places room inside container and returns remainder boxes") {
    val options = DungeonOptions(
      dimension = new Dimension(80, 80),
      roomOptions = RoomOptions(12, 12, maxAspectRatio = 2.0f, roomPadding = 1)
    )
    val decomposer = BoxDecomposer(options, Random(0))
    val outer = Box(5, 5, 45, 55)
    val (room, areas) = decomposer.createRoomInBox(outer)

    assert(outer.contains(room.box.getTopLeftCorner))
    assert(outer.contains(room.box.getBottomRightCorner))
    assert(room.box.getWidth >= options.roomOptions.minRoomDim)
    assert(room.box.getHeight >= options.roomOptions.minRoomDim)
    val unionAllFitsOuter = areas.forall { b =>
      outer.contains(b.getTopLeftCorner) && outer.contains(b.getBottomRightCorner)
    }
    assert(unionAllFitsOuter)
  }

  test("createRoomInBox is deterministic for fixed seed") {
    val options = DungeonOptions(
      dimension = new Dimension(60, 60),
      roomOptions = RoomOptions(10, 10, maxAspectRatio = 1.5f, roomPadding = 1)
    )
    val box = Box(0, 0, 40, 40)
    val a = BoxDecomposer(options, Random(42)).createRoomInBox(box)
    val b = BoxDecomposer(options, Random(42)).createRoomInBox(box)
    assert(a._1 == b._1)
    assert(a._2 == b._2)
  }
}
