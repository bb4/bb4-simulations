package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.simulation.dungeon.model.{DungeonModel, DungeonOptions}

trait DungeonGeneratorStrategy {
  def generateDungeon(options: DungeonOptions):DungeonModel
}
