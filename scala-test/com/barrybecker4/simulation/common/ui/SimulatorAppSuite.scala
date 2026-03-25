// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import org.scalatest.funsuite.AnyFunSuite

class SimulatorAppSuite extends AnyFunSuite {

  test("simulatorClassNameFromArgsUsesDefaultWhenEmpty") {
    assert(SimulatorApp.simulatorClassNameFromArgs(Array()) == SimulatorApplet.DEFAULT_SIMULATOR)
  }

  test("simulatorClassNameFromArgsUsesSingleArg") {
    assert(SimulatorApp.simulatorClassNameFromArgs(Array("a.b.MySim")) == "a.b.MySim")
  }

  test("simulatorClassNameFromArgsUsesSecondArgWhenMultiple") {
    val args = Array("-panel_class", "a.b.MySim", "-locale", "ENGLISH")
    assert(SimulatorApp.simulatorClassNameFromArgs(args) == "a.b.MySim")
  }
}
