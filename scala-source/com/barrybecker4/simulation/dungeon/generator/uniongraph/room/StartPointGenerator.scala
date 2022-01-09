// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.uniongraph.room

import scala.util.Random
import com.barrybecker4.simulation.dungeon.generator.uniongraph.room.StartPointGenerator.*


object StartPointGenerator {
  private val RAY_WIDTH = 3
  private val RND = Random(0)
}

class StartPointGenerator(rnd: Random = RND) {

  def getStartingPoints(startPos: Int, stopPos: Int): List[Int] = {
    var startingPoints: List[Int] = List()

    for (i <- startPos to stopPos - RAY_WIDTH by RAY_WIDTH) {
      startingPoints :+= i
    }
    rnd.shuffle(startingPoints)
  }
}
