// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.simulation.dungeon.generator.bsp.BoxSplitter
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random


class BoxSplitterSuite extends AnyFunSuite {

  val rnd: Random = Random(0)

  test("Split horizontally") {

    val splitter = BoxSplitter(15, 20, 3, rnd)
    val box = Box(2, 3, 20, 30)
    val (leftBox, rightBox) = splitter.splitHorizontally(box)

    assertResult((Box(2, 3, 20, 6), Box(2, 6, 20, 30))) {
      (leftBox, rightBox)
    }
  }

  test("Split vertically") {

    val splitter = BoxSplitter(15, 20, 3, rnd)
    val box = Box(2, 3, 20, 30)
    val (bottomBox, topBox) = splitter.splitVertically(box)

    assertResult((Box(10, 3, 20, 30), Box(2, 3, 10, 30))) {
      (bottomBox, topBox)
    }
  }

  test("Split horizontally when narrow") {

    val splitter = BoxSplitter(40, 50, 6, rnd)
    val box = Box(2, 3, 20, 30)
    val (leftBox, rightBox) = splitter.splitHorizontally(box)

    assertResult((Box(2, 3, 20, 12), Box(2, 12, 20, 30))) {
      (leftBox, rightBox)
    }
  }

  test("Split vertically when squat") {

    val splitter = BoxSplitter(40, 50, 6, rnd)
    val box = Box(2, 3, 20, 30)
    val (bottomBox, topBox) = splitter.splitVertically(box)

    assertResult((Box(10, 3, 20, 30), Box(2, 3, 10, 30))) {
      (bottomBox, topBox)
    }
  }
}
