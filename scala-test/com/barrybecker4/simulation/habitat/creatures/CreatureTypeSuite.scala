// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import org.scalatest.funsuite.AnyFunSuite

class CreatureTypeSuite extends AnyFunSuite {

  test("getPreys matches inverted predator map") {
    assert(
      CreatureType.LION.getPreys == Set(
        CreatureType.COW,
        CreatureType.RAT,
        CreatureType.CAT
      )
    )
    assert(CreatureType.GRASS.getPreys.isEmpty)
    assert(CreatureType.CAT.getPreys == Set(CreatureType.RAT))
    assert(CreatureType.COW.getPreys == Set(CreatureType.GRASS))
    assert(CreatureType.RAT.getPreys == Set(CreatureType.GRASS))
  }
}
