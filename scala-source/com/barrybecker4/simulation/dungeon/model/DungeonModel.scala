// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import java.awt.Dimension


/**
  * Represents the dungeon level as a graph.
  * It will have rooms - which are the nodes, and corridors - which are the endges.
  * The options determine how to generate the dungeon.
  * Once created, the dungeon can be rendered or operated on in other ways
  */
case class DungeonModel(rooms: Set[Room]) 
