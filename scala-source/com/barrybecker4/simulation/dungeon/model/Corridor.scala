// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.model

import com.barrybecker4.common.geometry.IntLocation


case class Corridor(paths: Seq[Path], rooms: Set[Room] = Set()) {

  def addCorridor(corridor: Corridor): Corridor = 
    Corridor(paths ++ corridor.paths, rooms ++ corridor.rooms)
}
