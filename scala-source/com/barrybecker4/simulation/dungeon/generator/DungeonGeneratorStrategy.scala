// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.simulation.dungeon.model.DungeonModel
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions


trait DungeonGeneratorStrategy {
  def generateDungeon(options: DungeonOptions): DungeonModel
}
