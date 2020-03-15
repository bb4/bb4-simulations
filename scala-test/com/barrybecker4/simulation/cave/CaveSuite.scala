// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.cave.model.Cave
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random


object CaveSuite {
  private val FLOOR = 0.2
  private val CEILING = 0.8
}

class CaveSuite extends AnyFunSuite {

  val RND: Random = new Random()
  RND.setSeed(0)

  test("3by3Construction") {
    val cave = new Cave(3, 3, CaveSuite.FLOOR, CaveSuite.CEILING, RND)
    assertResult("WWW\nWWC\nWWC\n") { cave.toString }
  }

  test("5by5Construction") {
    val cave = new Cave(5, 5, CaveSuite.FLOOR, CaveSuite.CEILING, RND)
    assertResult("CWWC \nWCCCC\n  WWW\n WWCW\n WW  \n") { cave.toString }
  }

  test("2by2Construction") {
    val cave = new Cave(2, 2, CaveSuite.FLOOR, CaveSuite.CEILING, RND)
    assertResult("W \n W\n") { cave.toString }
  }

  test("4by1Construction") {
    val cave = new Cave(4, 1, CaveSuite.FLOOR, CaveSuite.CEILING, RND)
    assertResult("WCCC\n") { cave.toString }
  }

  test("1by4Construction")  {
    val cave = new Cave(1, 4, CaveSuite.FLOOR, CaveSuite.CEILING, RND)
    assertResult("W\n \nW\nC\n") { cave.toString }
  }
}