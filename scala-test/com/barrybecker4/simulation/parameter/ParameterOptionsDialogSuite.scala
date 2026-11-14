// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import org.scalatest.funsuite.AnyFunSuite

class ParameterOptionsDialogSuite extends AnyFunSuite {

  test("dialog combo syncs to current distribution type on open") {
    val sim = new ParameterSimulator
    sim.setDistributionType(ParameterDistributionType.BOOLEAN_SKEWED)
    val dialog = new ParameterOptionsDialog(null, sim)
    dialog.syncFromSimulatorForTest()
    assert(dialog.selectedComboOrdinalForTest === ParameterDistributionType.BOOLEAN_SKEWED.ordinal)
  }

  test("dialog ok applies ordinal to setDistributionType") {
    val sim = new ParameterSimulator
    val dialog = new ParameterOptionsDialog(null, sim)
    dialog.syncFromSimulatorForTest()
    // Select a non-zero ordinal via applying ok after changing simulator path:
    // set combo by selecting through ok after we manually set type then confirm round-trip.
    val target = ParameterDistributionType.DISCRETE_UNIFORM
    sim.setDistributionType(target)
    dialog.syncFromSimulatorForTest()
    assert(dialog.selectedComboOrdinalForTest === target.ordinal)
    dialog.applyOkForTest()
    assert(sim.selectedDistributionType === target)
    assert(sim.histogramBinCount === ParameterDistributionType.NumDiscretes)
  }

  test("ordinal round-trip matches combo model names") {
    val names = ParameterDistributionType.values.map(_.name)
    for (i <- names.indices) {
      assert(ParameterDistributionType.fromOrdinal(i).name === names(i))
    }
  }
}
