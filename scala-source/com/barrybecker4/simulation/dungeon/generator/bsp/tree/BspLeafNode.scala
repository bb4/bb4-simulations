// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp.tree

import com.barrybecker4.simulation.dungeon.model.Room
import scala.collection.immutable.HashSet


case class BspLeafNode(room: Room) extends BspNode {

  def getRooms: Set[Room] = HashSet(room)

}
