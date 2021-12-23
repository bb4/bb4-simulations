// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp

import com.barrybecker4.simulation.dungeon.model.Room


case class BspBranchNode[T](
     direction: PartitionDirection,
     splitPosition: Int,
     partition1: BspNode[T],
     partition2: BspNode[T]) extends BspNode[T] {

  def getChildren: Set[T] =
    partition1.getChildren ++ partition2.getChildren

}
