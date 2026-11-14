// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures.populations

import org.scalatest.funsuite.AnyFunSuite

class HabitatResetSuite extends AnyFunSuite {

  test("reset twice with seed 1 yields the same creature locations and directions") {
    val habitat = new CatRatHabitat
    habitat.reset()
    val first = snapshot(habitat)
    habitat.reset()
    val second = snapshot(habitat)
    assert(first == second)
  }

  private def snapshot(habitat: Habitat): List[(String, Double, Double, Double)] =
    habitat.toList.flatMap { pop =>
      pop.creatures.toList
        .sortBy(c => (c.getLocation.x, c.getLocation.y, c.getDirection))
        .map(c => (c.getName, c.getLocation.x, c.getLocation.y, c.getDirection))
    }
}
