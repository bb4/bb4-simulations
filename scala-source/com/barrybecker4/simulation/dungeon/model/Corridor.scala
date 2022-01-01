package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.common.geometry.IntLocation


case class Corridor(paths: Seq[(IntLocation, IntLocation)], rooms: Set[Room] = Set()) {

  def addCorridor(corridor: Corridor): Corridor = 
    Corridor(paths ++ corridor.paths, rooms ++ corridor.rooms)
}
