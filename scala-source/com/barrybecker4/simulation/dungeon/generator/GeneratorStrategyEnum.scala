// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.simulation.dungeon.generator.bsp.BspDungeonGenerator
import com.barrybecker4.simulation.dungeon.generator.uniongraph.UnionGraphDungeonGenerator
import com.barrybecker4.simulation.dungeon.generator.organic.OrganicDungeonGenerator


enum GeneratorStrategyEnum(val generator: DungeonGeneratorStrategy) {

  case UnionGraph extends GeneratorStrategyEnum(UnionGraphDungeonGenerator())
  case BinarySpacePartition extends GeneratorStrategyEnum(BspDungeonGenerator())
  case Organic extends GeneratorStrategyEnum(OrganicDungeonGenerator())
}

end GeneratorStrategyEnum
