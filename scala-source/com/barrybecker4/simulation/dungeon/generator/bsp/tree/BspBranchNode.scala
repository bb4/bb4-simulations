// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp.tree

import com.barrybecker4.simulation.dungeon.model.{Orientation, Room}


case class BspBranchNode(
                          direction: Orientation,
                          splitPosition: Int,
                          partition1: BspNode,
                          partition2: BspNode) extends BspNode {

  def getRooms: Set[Room] =
    partition1.getRooms ++ partition2.getRooms
  
}
