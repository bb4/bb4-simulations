package com.barrybecker4.simulation.dungeon.generator.organic.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.Room


case class SproutLocationFinder(bounds: Box) {

  def findLocations(room: Room): Set[SproutLocation] = {
    Set()
  }
}
