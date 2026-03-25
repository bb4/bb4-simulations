// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model.util

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.Orientation.Horizontal
import com.barrybecker4.simulation.dungeon.model.{Corridor, DungeonMap, Path, Room}
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Dimension

class IntersectorSuite extends AnyFunSuite {

  private val dim = new Dimension(20, 20)

  test("ray leaves grid -> None") {
    val empty = DungeonMap(DungeonMap.initializeFromRooms(Set.empty))
    val source = Room(Box(1, 1, 3, 4))
    val hit = Intersector(dim, empty).checkForIntersection(IntLocation(1, 1), source, -1, 0)
    assert(hit.isEmpty)
  }

  test("ray hits corridor (middle strip cell is corridor)") {
    val room = Room(Box(3, 1, 5, 4))
    val path = Path(IntLocation(4, 5), Horizontal, 2)
    val corridor = Corridor(Seq(path), Set(room))
    val dm = DungeonMap(DungeonMap.initializeFromRooms(Set(room))).addCorridor(corridor)
    val hit = Intersector(dim, dm).checkForIntersection(IntLocation(3, 3), room, 1, 0)
    assert(hit.isDefined)
    assert(hit.get._2.isInstanceOf[Corridor])
  }

  test("ray hits room when flank strip cells are rooms") {
    val left = Room(Box(2, 1, 5, 3))
    val right = Room(Box(2, 6, 5, 9))
    val dm = new DungeonMap(Set(left, right))
    val hit = Intersector(dim, dm).checkForIntersection(IntLocation(2, 3), left, 1, 0)
    assert(hit.isDefined)
    assert(hit.get._2 == right)
  }

  test("ray exits top boundary on empty map") {
    val empty = DungeonMap(DungeonMap.initializeFromRooms(Set.empty))
    val source = Room(Box(5, 5, 8, 8))
    val hit = Intersector(dim, empty).checkForIntersection(IntLocation(6, 6), source, 0, -1)
    assert(hit.isEmpty)
  }
}
