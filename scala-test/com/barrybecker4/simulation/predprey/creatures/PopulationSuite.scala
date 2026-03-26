// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.creatures

import org.scalatest.funsuite.AnyFunSuite

class PopulationSuite extends AnyFunSuite:

  test("setPopulation clamps to non-negative integers") {
    val p = new Foxes
    p.setPopulation(-12.3)
    assert(p.getPopulation === 0)
  }

  test("setPopulation repeatedly scales down until at or below max") {
    val p = new Rabbits
    p.setPopulation(200_000.0)
    assert(p.getPopulation <= 100_000)
    assert(p.getPopulation >= 0)
  }
