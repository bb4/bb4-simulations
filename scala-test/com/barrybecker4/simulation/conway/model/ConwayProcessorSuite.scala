// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.simulation.conway.model.ConwayProcessor.RuleType
import org.scalatest.funsuite.AnyFunSuite

class ConwayProcessorSuite extends AnyFunSuite {

  test("TraditionalB3S23 blinker oscillates to horizontal") {
    val p = new ConwayProcessor(useParallel = false)
    p.clearGridForTesting()
    p.setWrap(false, 5, 5)
    p.setRuleType(RuleType.TraditionalB3S23)

    p.setAlive(1, 2)
    p.setAlive(2, 2)
    p.setAlive(3, 2)

    p.nextPhase()

    // Surviving cells increment age (+1); newly born cells start at 1.
    assert(p.getValue(IntLocation(2, 1)) == 1)
    assert(p.getValue(IntLocation(2, 2)) == 2)
    assert(p.getValue(IntLocation(2, 3)) == 1)
    assert(p.getValue(IntLocation(1, 2)) == 0)
    assert(p.getValue(IntLocation(3, 2)) == 0)
  }
}
