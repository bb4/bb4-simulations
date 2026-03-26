// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.propagation.PropagatorState
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Dimension
import java.awt.image.BufferedImage

class WfcModelRunWithLimitSuite extends AnyFunSuite {

  private final class StubPropagator(tCounter: Int, compatLen: Int) extends PropagatorState {
    locally {
      for (d <- 0 to 3) {
        state(d) = Array.fill(tCounter)(null)
        for (t <- 0 until tCounter)
          state(d)(t) = Array.fill(compatLen)(0)
      }
    }
  }

  private class AdvanceOverrideModel(outcome: Option[Boolean])
      extends WfcModel("stub", 1, 1, limit = 100, allowInconsistencies = true) {

    dimensions = new Dimension(1, 1)
    tCounter = 1
    weights = Array(1.0)
    propagator = new StubPropagator(1, 1)

    override def onBoundary(x: Int, y: Int): Boolean = false

    override def graphics(): BufferedImage =
      new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)

    override def advance(steps: Int): Option[Boolean] = outcome
  }

  test("runWithLimit returns true only when advance returns Some(true)") {
    assert(new AdvanceOverrideModel(Some(true)).runWithLimit(0, 1))
    assert(!new AdvanceOverrideModel(Some(false)).runWithLimit(0, 1))
    assert(!new AdvanceOverrideModel(None).runWithLimit(0, 1))
  }
}
