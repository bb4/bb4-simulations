// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.model.Orientation.Horizontal
import org.scalatest.funsuite.AnyFunSuite

class DungeonMapSuite extends AnyFunSuite {

  private val roomA = Room(Box(0, 0, 3, 4))
  private val roomB = Room(Box(0, 6, 3, 10))

  test("initializeFromRooms maps interior cells to room") {
    val cells = DungeonMap.initializeFromRooms(Set(roomA))
    assert(cells.get(IntLocation(0, 0)).contains(roomA))
    assert(cells.get(IntLocation(2, 3)).contains(roomA))
    assert(cells.get(IntLocation(3, 0)).isEmpty)
  }

  test("apply and isRoom / isCorridor") {
    val dm = new DungeonMap(Set(roomA))
    assert(dm(IntLocation(1, 2)).contains(roomA))
    assert(dm.isRoom(IntLocation(1, 2)))
    assert(!dm.isCorridor(IntLocation(1, 2)))
    assert(dm(IntLocation(99, 99)).isEmpty)
  }

  test("getRooms and getCorridors") {
    val path = Path(IntLocation(0, 4), Horizontal, 2)
    val corridor = Corridor(Seq(path), Set(roomA, roomB))
    val dm = DungeonMap(DungeonMap.initializeFromRooms(Set(roomA, roomB)))
      .addCorridor(corridor)
    assert(dm.getRooms == Set(roomA, roomB))
    assert(dm.getCorridors == Set(corridor))
  }

  test("isEmptyRegion") {
    val dm = new DungeonMap(Set(roomA))
    assert(dm.isEmptyRegion(Box(10, 10, 12, 12)))
    assert(!dm.isEmptyRegion(Box(0, 0, 2, 2)))
  }

  test("isConnected when two rooms share a corridor") {
    val path = Path(IntLocation(0, 4), Horizontal, 2)
    val corridor = Corridor(Seq(path), Set(roomA, roomB))
    val dm = newDungeonWithRoomsAndCorridor(Set(roomA, roomB), corridor)
    assert(dm.isConnected(roomA, roomB))
  }

  test("isConnected false without shared corridor") {
    val dm = new DungeonMap(Set(roomA, roomB))
    assert(!dm.isConnected(roomA, roomB))
  }

  test("horizontal path cells map to corridor") {
    val path = Path(IntLocation(1, 1), Horizontal, 3)
    val corridor = Corridor(Seq(path), Set(roomA))
    val dm = DungeonMap(Map.empty[IntLocation, Room | Corridor]).addCorridor(corridor)
    assert(dm.isCorridor(IntLocation(1, 1)))
    assert(dm.isCorridor(IntLocation(1, 2)))
    assert(!dm.isCorridor(IntLocation(1, 4)))
  }

  test("vertical path cells map to corridor") {
    val path = Path(IntLocation(1, 1), Orientation.Vertical, 2)
    val corridor = Corridor(Seq(path), Set(roomA))
    val dm = DungeonMap(Map.empty[IntLocation, Room | Corridor]).addCorridor(corridor)
    assert(dm.isCorridor(IntLocation(1, 1)))
    assert(dm.isCorridor(IntLocation(2, 1)))
    assert(!dm.isCorridor(IntLocation(3, 1)))
  }

  private def newDungeonWithRoomsAndCorridor(rooms: Set[Room], corridor: Corridor): DungeonMap =
    DungeonMap(DungeonMap.initializeFromRooms(rooms)).addCorridor(corridor)
}
