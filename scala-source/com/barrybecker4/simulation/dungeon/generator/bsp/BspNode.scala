package com.barrybecker4.simulation.dungeon.generator.bsp

import com.barrybecker4.simulation.dungeon.model.Room


case class BspNode[T](
  direction: Option[PartitionDirection],
  splitPosition: Option[Int],
  partition1: Option[BspNode[T]],
  partition2: Option[BspNode[T]],
  data: Option[T] = None) {

  def this(item: T) = this(None, None, None, None, Some(item))

  def this(direction: PartitionDirection, splitPosition: Int, partition1: BspNode[T], partition2: BspNode[T]) =
    this(Some(direction), Some(splitPosition), Some(partition1), Some(partition2))

  def getChildren: Set[T] = {
    if (direction.isEmpty) data.toSet
    else partition1.get.getChildren ++ partition2.get.getChildren
  }

}
