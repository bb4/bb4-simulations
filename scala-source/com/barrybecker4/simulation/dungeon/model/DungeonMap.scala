package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.common.geometry.IntLocation
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
    var m = cellToStructure
    for (x <- topLeft.getX to bottomRight.getX) {
      for (y <- topLeft.getY to bottomRight.getY) {
        m += IntLocation(y, x) -> room
      }
    }
    m
  }
}

case class DungeonMap(cellToStructure: Map[IntLocation, Room | Corridor]) {

  def this(rooms: Set[Room]) = {
    this(DungeonMap.initializeFromRooms(rooms))
  }

  def apply(x: Int, y: Int): Room | Corridor = cellToStructure(IntLocation(y, x))

  def update(roomSet: RoomSet): DungeonMap = {
    var dmap: DungeonMap = this

    for (room <- roomSet.rooms) {
      dmap = addRoom(room)
    }
    addCorridors(roomSet.corridors)
  }

  private def addRoom(room: Room): DungeonMap =
    DungeonMap(DungeonMap.addRoom(room, cellToStructure))

  private def addCorridors(corridors: Set[Corridor]): DungeonMap = {
    var dmap: DungeonMap = this
    for (c <- corridors) {
      dmap = addCorridor(c)
    }
    dmap
  }

  private def addCorridor(corridor: Corridor): DungeonMap = {
    var dmap: DungeonMap = this
    for (path <- corridor.paths)
      dmap = addPath(path, corridor)
    dmap
  }

  private def addPath(path: (IntLocation, IntLocation), corridor: Corridor): DungeonMap = {
    var dmap: DungeonMap = this
    val x1 = path._1.getX
    val y1 = path._1.getY
    val x2 = path._2.getX
    val y2 = path._2.getY
    if (y1 == y2) {
      for (x <- x1 to x2)
        dmap = DungeonMap(cellToStructure + (IntLocation(x, y1) -> corridor))
    } else {
      for (y <- y1 to y2)
        dmap = DungeonMap(cellToStructure + (IntLocation(x1, y) -> corridor))
    }
    dmap
  }

  // todo
  def getCorridors: Set[Corridor] = Set()
}
