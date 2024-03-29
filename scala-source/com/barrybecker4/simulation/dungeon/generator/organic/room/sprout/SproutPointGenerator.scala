// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.organic.room.sprout

import com.barrybecker4.simulation.dungeon.generator.organic.room.sprout.SproutPointGenerator.*
import scala.util.Random


object SproutPointGenerator {
  private val MIN_SEPARATION = 5
  private val RND = Random(0)
}

class SproutPointGenerator(rnd: Random = RND) {

  def getSproutPoints(startPos: Int, stopPos: Int): List[Int] = {
    val len = stopPos - startPos
    if (len <= 2 * MIN_SEPARATION) {
      return List(startPos + len / 2)
    }
    var startingPoints: List[Int] = List()
    var i = startPos + 1
    while (i < stopPos - MIN_SEPARATION) {
      i += rnd.nextInt(MIN_SEPARATION)
      startingPoints :+= i
      i += MIN_SEPARATION
    }
    startingPoints
  }
}
