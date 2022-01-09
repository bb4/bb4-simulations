// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.organic.room.sprout

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.simulation.dungeon.model.{Orientation, Room}

case class SproutLocation(
  room: Room,
  position: IntLocation,
  orientation: Orientation,
  direction: Int)
