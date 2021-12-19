// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions, Room}

import scala.collection.immutable.HashSet
import java.awt.{Color, Dimension}


class DungeonGenerator {

  private val roomGenerator = RoomGenerator()

  def generateDungeon(options: DungeonOptions):DungeonModel = {

    val rooms = roomGenerator.generateRooms(options)
    new DungeonModel(rooms)
  }
}
