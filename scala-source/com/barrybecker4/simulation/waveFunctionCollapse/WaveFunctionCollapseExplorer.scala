// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.waveFunctionCollapse.WaveFunctionCollapseExplorer.DEFAULT_STEPS_PER_FRAME
import com.barrybecker4.simulation.waveFunctionCollapse.model.WfcModel

import javax.swing._
import java.awt._
import java.awt.event.{ComponentAdapter, ComponentEvent}


/**
  * Interactively explores generating Wave Function Collapse procedural modeling.
  * @author Barry Becker.
  */
object WaveFunctionCollapseExplorer {
  protected val INITIAL_TIME_STEP = 10.0
  protected val DEFAULT_STEPS_PER_FRAME = 100
}

class WaveFunctionCollapseExplorer()
  extends Simulator("Wave Function Collapse Explorer") {

  private var wfcModel: WfcModel = _
  private var options: DynamicOptions = _
  commonInit()

  private def commonInit(): Unit = {
    initCommonUI()
    val self = this
    this.addComponentListener(new ComponentAdapter {
      override def componentResized(ce: ComponentEvent): Unit = {
        println("resized so rerunning...")
        options.setDimensions(self.getSize())
      }
    })
  }

  def setModel(m: WfcModel): Unit = {
    wfcModel = m
  }

  override protected def reset(): Unit = {
    if (options != null) options.reset()
    commonInit()
  }

  override protected def createOptionsDialog = new OptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = 1

  override def timeStep: Double = {
    if (!isPaused && wfcModel != null) {
      wfcModel.advance(DEFAULT_STEPS_PER_FRAME)
    }
    tStep
  }

  override def paint(g: Graphics): Unit = {
    if (g == null || wfcModel == null || !wfcModel.isReady) return
    super.paint(g)
    Profiler.getInstance.startRenderingTime()
    g.drawImage(wfcModel.graphics(), 0, 0, null)
    Profiler.getInstance.stopRenderingTime()
  }

  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    options.setDimensions(this.getSize())
    setPaused(false)
    options
  }
}