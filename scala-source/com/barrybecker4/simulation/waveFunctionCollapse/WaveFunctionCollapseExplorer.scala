// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse

import com.barrybecker4.simulation.cave.model.CaveModel
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}

import javax.swing._
import java.awt._


/**
  * Interactively explores generating cave systems.
  * @author Barry Becker.
  */
object WaveFunctionCollapseExplorer {
  protected val INITIAL_TIME_STEP = 10.0
  protected val DEFAULT_STEPS_PER_FRAME = 10
}

class WaveFunctionCollapseExplorer() extends Simulator("Wave Function Collapse Explorer") {

  commonInit()


  private def commonInit(): Unit = {
    BatchRun.main(Array())
  }

  override protected def reset(): Unit = {
    commonInit()
  }

  override protected def getInitialTimeStep: Double = 0.0

  override protected def createOptionsDialog: SimulatorOptionsDialog = new SimulatorOptionsDialog(null, null) {
    /** For custom parameters that don't fall in other categories. */
    override protected def createCustomParamPanel: JPanel = new JPanel()
  }

  override def timeStep: Double = 0.1
}
