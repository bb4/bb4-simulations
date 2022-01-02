// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.simulation.dungeon.generator.bsp.room.RoomToCorridorsMap
import com.barrybecker4.simulation.dungeon.generator.bsp.tree.BspNode

import java.awt.Dimension


/**
  * Represents the dungeon level as a graph.
  * It will have rooms - which are the nodes, and corridors - which are the edges connecting rooms.
  * The DungeonOptions determine how to generate the dungeon.
  * Once created, the dungeon can be rendered or operated on in other ways
  */
case class DungeonModel(rooms: Set[Room], corridors: Set[Corridor], dungeonMap: Option[DungeonMap] = None)

