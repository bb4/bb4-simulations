// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import org.scalatest.funsuite.AnyFunSuite

import javax.vecmath.Point2d


class CreatureSuite extends AnyFunSuite {


  test("Creature serialization") {

    val creature = new Creature(CreatureType.CAT, new Point2d(2, 3))

    assertResult("cat hunger=0 pregnant=0 alive=true") {
      creature.toString
    }
  }

  test("absMod") {
    assertResult(Seq(0.20000000000000018, -0.19999999999999996, 0.20000000000000018, -0.20000000000000018)) {
      Seq(1.2, -1.2, 2.2, -2.2).map(v => absMod(v))
    }
  }

}
