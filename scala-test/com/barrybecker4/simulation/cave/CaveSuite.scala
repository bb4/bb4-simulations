// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.cave.model.Cave
import org.scalatest.FunSuite


object CaveSuite {
  private val FLOOR = 0.2
  private val CEILING = 0.8
}

class CaveSuite extends FunSuite {

  test("2by2Construction") {
    val cave = new Cave(2, 2, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("WW\nWW\n") { cave.toString }
  }

  test("3by3Construction") {
    val cave = new Cave(3, 3, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("WWW\nWWC\nWWC\n") { cave.toString }
  }

  test("5by5Construction") {
    val cave = new Cave(5, 5, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("WWWCC\nWW  W\nWC WW\nWC WW\nWCWWC\n") { cave.toString }
  }

  test("4by1Construction") {
    val cave = new Cave(4, 1, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("WWWW\n") { cave.toString }
  }

  test("1by4Construction")  {
    val cave = new Cave(1, 4, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("W\nW\nW\nW\n") { cave.toString }
  }
}
