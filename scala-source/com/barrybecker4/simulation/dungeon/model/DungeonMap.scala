// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.common.geometry.{Box, IntLocation}
import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.RoomSet


object DungeonMap {

  def initializeFromRooms(rooms: Set[Room]): Map[IntLocation, Room | Corridor] = {
    var cellToStructure: Map[IntLocation, Room | Corridor] = Map()
    for (room <- rooms) {
      cellToStructure = addRoom(room, cellToStructure)
    }
    cellToStructure
  }

  private def addRoom(room: Room,
                      cellToStructure: Map[IntLocation, Room | Corridor]): Map[IntLocation, Room | Corridor] = {
    val topLeft = room.box.getTopLeftCorner
    val bottomRight = room.box.getBottomRightCorner
    var map = cellToStructure
    for (x <- topLeft.getX until bottomRight.getX) {
      for (y <- topLeft.getY until bottomRight.getY) {
        map += IntLocation(y, x) -> room
      }
    }
    map
  }

  private def addCorridor(corridor: Corridor,
                          cellToStructure: Map[IntLocation, Room | Corridor]): Map[IntLocation, Room | Corridor] = {
    var map = cellToStructure
    for (path <- corridor.paths) {
      val x1 = path.start.getX
      val y1 = path.start.getY

      if (path.orientation == Orientation.Horizontal) {
        for (x <- x1 until x1 + path.length)
          map += IntLocation(y1, x) -> corridor
      } else {
        for (y <- y1 until y1 + path.length)
          map += IntLocation(y, x1) -> corridor
      }
    }
    map
  }
}

case class DungeonMap(cellToStructure: Map[IntLocation, Room | Corridor]) {

  def this(rooms: Set[Room]) = {
    this(DungeonMap.initializeFromRooms(rooms))
  }

  def apply(pos: IntLocation): Option[Room | Corridor] = cellToStructure.get(pos)

  def isRoom(pos: IntLocation): Boolean =
    cellToStructure.get(pos).exists {
      case _: Room => true
      case _       => false
    }

  def isCorridor(pos: IntLocation): Boolean =
    cellToStructure.get(pos).exists {
      case _: Corridor => true
      case _           => false
    }

  def update(roomSet: RoomSet): DungeonMap = {
    var map: Map[IntLocation, Room | Corridor] = cellToStructure

    for (room <- roomSet.rooms) {
      map = DungeonMap.addRoom(room, map)
    }
    for (corridor <- roomSet.corridors) {
      map = DungeonMap.addCorridor(corridor, map)
    }
    DungeonMap(map)
  }

  def addRoom(room: Room): DungeonMap =
    DungeonMap(DungeonMap.addRoom(room, cellToStructure))

  def addCorridors(corridors: Set[Corridor]): DungeonMap = {
    var map: Map[IntLocation, Room | Corridor] = cellToStructure
    for (c <- corridors)
      map = DungeonMap.addCorridor(c, map)
    DungeonMap(map)
  }

  def addCorridor(corridor: Corridor): DungeonMap =
    DungeonMap(DungeonMap.addCorridor(corridor, cellToStructure))

  def isEmptyRegion(box: Box): Boolean = {
    val topLeft = box.getTopLeftCorner
    val bottomRight = box.getBottomRightCorner
    (topLeft.getX to bottomRight.getX).forall { x =>
      (topLeft.getY to bottomRight.getY).forall { y =>
        this(IntLocation(y, x)).isEmpty
      }
    }
  }
  
  // this could be optimized by just considering corridors connected to room1
  def isConnected(room1: Room, room2: Room): Boolean = {
    val corridors = getCorridors
    corridors.exists(c => c.rooms.contains(room1) && c.rooms.contains(room2))
  }

  val getRooms: Set[Room] =
    cellToStructure.values.collect { case r: Room => r }.toSet

  val getCorridors: Set[Corridor] =
    cellToStructure.values.collect { case c: Corridor => c }.toSet

  def getCorridorCells: Set[IntLocation] = cellToStructure.keys.toSet.filter(key => isCorridor(key))
}
