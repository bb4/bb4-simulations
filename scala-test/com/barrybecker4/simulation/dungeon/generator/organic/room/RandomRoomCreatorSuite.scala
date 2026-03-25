// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.organic.room

import org.scalatest.funsuite.AnyFunSuite

class RandomRoomCreatorSuite extends AnyFunSuite {

  private val ar = 2.0f

  test("adjustToMaxAspect clamps wide rectangle") {
    val d = RandomRoomCreator.adjustToMaxAspect(10, 3, ar)
    assert(d.width <= 6)
    assert(d.height == 3)
  }

  test("adjustToMaxAspect clamps tall rectangle") {
    val d = RandomRoomCreator.adjustToMaxAspect(3, 10, ar)
    assert(d.width == 3)
    assert(d.height <= 6)
  }

  test("adjustToMaxAspect leaves square unchanged") {
    val d = RandomRoomCreator.adjustToMaxAspect(5, 5, ar)
    assert(d.width == 5)
    assert(d.height == 5)
  }

  test("exceedsMaxAspect detects violation") {
    assert(RandomRoomCreator.exceedsMaxAspect(10, 3, ar))
    assert(RandomRoomCreator.exceedsMaxAspect(3, 10, ar))
    assert(!RandomRoomCreator.exceedsMaxAspect(4, 5, ar))
  }
}
