// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey

import org.scalatest.funsuite.AnyFunSuite

class PredPreyDynamicsSuite extends AnyFunSuite:

  test("nextFoxCount does not divide by zero when rabbits are zero") {
    val v = PredPreyDynamics.nextFoxCount(100, 0, birthRate = 1.2, deathRate = 10.0)
    assert(!v.isInfinity && !v.isNaN)
    // starvation term uses denominator 1: 100*1.2 - 100*10/1
    assert(v == -880.0)
  }

  test("nextRabbitCount matches closed form for sample values") {
    val v = PredPreyDynamics.nextRabbitCount(100, 50, birthRate = 1.2, deathRate = 0.01)
    assert(v == 100.0 * 1.2 - 50 * 0.01 * 100.0)
  }
