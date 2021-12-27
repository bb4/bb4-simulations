// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp.room

import com.barrybecker4.simulation.dungeon.model.{Corridor, Room}

import scala.collection.immutable.HashSet


class RoomToCorridorsMap {

  private var roomToCorridors: Map[Room, Set[Corridor]] = Map()

  def get(room: Room): Set[Corridor] = roomToCorridors(room)

  def addCorridorToMap(room: Room, corridor: Corridor): Unit =
    roomToCorridors += room -> (roomToCorridors.getOrElse(room, Set()) ++ HashSet(corridor))

  def getAllCorridors: Set[Corridor] = roomToCorridors.values.flatten.toSet

}
