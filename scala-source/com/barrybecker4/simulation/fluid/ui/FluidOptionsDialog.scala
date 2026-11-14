// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.fluid.ui

import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._


/**
  * Fluid-specific options dialog. Dynamic controls live on the side panel;
  * no additional custom parameters here.
  */
class FluidOptionsDialog private[ui](parent: Component, simulator: FluidSimulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  override protected def createCustomParamPanel: JPanel = new JPanel
}
