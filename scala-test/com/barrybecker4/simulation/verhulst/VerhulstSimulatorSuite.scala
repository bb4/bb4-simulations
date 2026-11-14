// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.graphing.GraphOptionsDialog
import com.barrybecker4.ui.util.Log
import org.scalatest.funsuite.AnyFunSuite


class VerhulstSimulatorSuite extends AnyFunSuite {

  private def ensureAppContext(): Unit = {
    if (!AppContext.isInitialized) {
      AppContext.initialize(
        "ENGLISH",
        List("com.barrybecker4.ui.message", "com.barrybecker4.simulation.common.ui.message"),
        new Log
      )
    }
  }

  test("options dialog is SimulatorOptionsDialog, not GraphOptionsDialog") {
    ensureAppContext()
    class Probe extends VerhulstSimulator {
      def optionsDialogForTest: SimulatorOptionsDialog = createOptionsDialog
    }
    val dialog = new Probe().optionsDialogForTest
    assert(dialog.isInstanceOf[SimulatorOptionsDialog])
    assert(!dialog.isInstanceOf[GraphOptionsDialog])
  }
}
