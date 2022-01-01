// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp.room

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.model.{Room, RoomOptions}
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random


class BoxSplitterSuite extends AnyFunSuite {

  val rnd: Random = Random(0)

  test("Split horizontally") {

    val roomOptions = RoomOptions(15, 20, 100, 3)
    val splitter = BoxSplitter(roomOptions, rnd)
    val box = Box(2, 3, 20, 30)
    val (leftBox, rightBox) = splitter.splitHorizontally(box)

    assertResult((Box(2, 3, 20, 12), Box(2, 12, 20, 30))) {
      (leftBox, rightBox)
    }
  }

  test("Split vertically") {

    val roomOptions = RoomOptions(15, 20, 100, 3)
    val splitter = BoxSplitter(roomOptions, rnd)
    val box = Box(2, 3, 20, 30)
    val (bottomBox, topBox) = splitter.splitVertically(box)

    assertResult((Box(11, 3, 20, 30), Box(2, 3, 11, 30))) {
      (bottomBox, topBox)
    }
  }

  test("Split horizontally when narrow") {

    val roomOptions = RoomOptions(40, 30, 100, 3)
    val splitter = BoxSplitter(roomOptions, rnd)
    val box = Box(2, 3, 20, 30)
    val (leftBox, rightBox) = splitter.splitHorizontally(box)

    assertResult((Box(2, 3, 20, 21), Box(2, 21, 20, 30))) {
      (leftBox, rightBox)
    }
  }

  test("Split vertically when squat") {

    val roomOptions = RoomOptions(40, 50, 100, 3)
    val splitter = BoxSplitter(roomOptions, rnd)
    val box = Box(2, 3, 20, 30)
    val (bottomBox, topBox) = splitter.splitVertically(box)

    assertResult((Box(11, 3, 20, 30), Box(2, 3, 11, 30))) {
      (bottomBox, topBox)
    }
  }

  test("Set uniqueness for 2 rooms with same values") {
    var set: Set[Room] = Set()
    set += Room(Box(1, 1, 2, 2))
    set += Room(Box(1, 1, 2, 2))
    assertResult(1) { set.size }
  }

  test("Set uniqueness for 2 identical rooms") {
    var set: Set[Room] = Set()
    val room = Room(Box(1, 1, 2, 2))
    set += room
    set += room
    assertResult(1) { set.size }
  }
}
