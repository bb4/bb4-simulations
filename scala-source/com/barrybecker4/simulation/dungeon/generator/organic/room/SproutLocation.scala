package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.simulation.dungeon.model.{Orientation, Room}
import com.barrybecker4.common.geometry.IntLocation

case class SproutLocation(
  room: Room, 
  position: IntLocation,
  orientation: Orientation, 
  direction: Int) 