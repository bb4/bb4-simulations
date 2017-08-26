// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.cave1.model.Cave
import org.scalatest.FunSuite


object CaveSuite {
  private val FLOOR = 0.1
  private val CEILING = 0.9
}

class CaveSuite extends FunSuite {

  test("2by2Construction") {
    val cave = new Cave(2, 2, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("CC\nCC\n") { cave.toString }
  }

  test("3by3Construction") {
    val cave = new Cave(3, 3, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("CCC\nCCW\nCCC\n") { cave.toString }
  }

  test("5by5Construction") {
    val cave = new Cave(5, 5, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("CCCWW\nCCCCC\nCWCCC\nCCCCC\nCWCCC\n") { cave.toString }
  }

  test("4by1Construction") {
    val cave = new Cave(4, 1, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("CCCC\n") { cave.toString }
  }

  test("1by4Construction")  {
    val cave = new Cave(1, 4, CaveSuite.FLOOR, CaveSuite.CEILING)
    assertResult("C\nC\nC\nC\n") { cave.toString }
  }
}
