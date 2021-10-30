/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion

import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._
import java.awt.event.ActionEvent


/**
  * @author Barry Becker
  */
class RDOptionsDialog private[reactiondiffusion](parent: Component, simulator: Simulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  private var offscreenRenderingCheckbox: JCheckBox = _
  private var showProfilingCheckbox: JCheckBox = _
  private var useParallelRenderingCheckbox: JCheckBox = _

  override protected def createCustomParamPanel: JPanel = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    val sim = getSimulator.asInstanceOf[RDSimulator]
    showProfilingCheckbox = createCheckBox("Show Profiling Information",
      "If checked, profiling statistics will be displayed in the console when paused.",
      RDProfiler.getInstance.isEnabled)
    offscreenRenderingCheckbox = createCheckBox("Use offscreen rendering",
      "If checked, rendering graphics to an offscreen buffer before copying to the screen.",
      sim.getUseOffScreenRendering)
    useParallelRenderingCheckbox = createCheckBox("Use parallel rendering",
      "Rendering will take advantage of as many cores/threads that are avaialble.",
      sim.getRenderingOptions.isParallelized)
    panel.add(showProfilingCheckbox)
    panel.add(offscreenRenderingCheckbox)
    panel.add(useParallelRenderingCheckbox)
    panel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    val source = e.getSource
    val sim = getSimulator.asInstanceOf[RDSimulator]
    if (source eq showProfilingCheckbox) RDProfiler.getInstance.setEnabled(showProfilingCheckbox.isSelected)
    else if (source eq offscreenRenderingCheckbox) sim.setUseOffscreenRendering(offscreenRenderingCheckbox.isSelected)
    else if (source eq useParallelRenderingCheckbox) sim.getRenderingOptions.setParallelized(useParallelRenderingCheckbox.isSelected)
  }
}
