// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.simulation.dungeon.generator.DungeonGeneratorStrategy
import com.barrybecker4.simulation.dungeon.generator.bsp.corridor.CorridorGenerator
import com.barrybecker4.simulation.dungeon.generator.bsp.room.RoomGenerator
import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions, Room}

import java.awt.{Color, Dimension}
import scala.util.Random


class BspDungeonGenerator extends DungeonGeneratorStrategy {

  def generateDungeon(options: DungeonOptions): DungeonModel = {
    val rnd: Random = Random(0)
    
    val roomGenerator = RoomGenerator(options, rnd)
    val corridorGenerator = CorridorGenerator(options)

    val bspTree = roomGenerator.generateRooms()
    val roomsToCorridors = corridorGenerator.generateCorridors(bspTree)

    DungeonModel(bspTree.getRooms, roomsToCorridors.getAllCorridors)
  }
}
