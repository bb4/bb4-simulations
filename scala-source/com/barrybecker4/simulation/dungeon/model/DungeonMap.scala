package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.common.geometry.IntLocation


class DungeonMap(rooms: Set[Room]) {

  private var cellToStructure: Map[IntLocation, Room | Corridor] = Map()

  initialize()

  def set(x: Int, y: Int, room: Room | Corridor): Unit =
    cellToStructure += IntLocation(y, x) -> room

  def apply(x: Int, y: Int): Room | Corridor = cellToStructure(IntLocation(y, x))

  private def initialize(): Unit = {
    for (room <- rooms) {
      addRoom(room)
    }
  }

  private def addRoom(room: Room): Unit = {
    val topLeft = room.box.getTopLeftCorner
    val bottomRight = room.box.getBottomRightCorner
    for (x <- topLeft.getX to bottomRight.getX) {
      for (y <- topLeft.getY to bottomRight.getY) {
        set(x, y, room)
      }
    }
  }
  
  private def addCorridor(corridor: Corridor): Unit = {
    val path = corridor.path
    
  }
  
  def getCorridors: Set[Corridor] = Set()
}
