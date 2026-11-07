// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import org.scalatest.funsuite.AnyFunSuite

/** Fluid has no parameter descriptors, so optimization must not call the optimizer. */
class FluidOptimizationSuite extends AnyFunSuite {

  test("fluid optimization is not configured and does not throw") {
    val simulator = new FluidSimulator
    simulator.doOptimization()
  }
}
