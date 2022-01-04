package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.simulation.dungeon.model.DungeonModel
import com.barrybecker4.simulation.dungeon.model.options.DungeonOptions


object DungeonGeneratorStrategy {
  enum GeneratorStrategyType: 
    case UnionGraph, BinarySpacePartition
}

trait DungeonGeneratorStrategy {
  def generateDungeon(options: DungeonOptions): DungeonModel
}
