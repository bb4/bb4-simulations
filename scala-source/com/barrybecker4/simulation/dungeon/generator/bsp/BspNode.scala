package com.barrybecker4.simulation.dungeon.generator.bsp

import com.barrybecker4.simulation.dungeon.model.Room


case class BspNode(
  direction: PartitionDirection,
  partition1: BspNode,
  partition2: BspNode,
  children: Set[Room])
