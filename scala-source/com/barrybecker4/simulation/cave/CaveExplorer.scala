// Copyright by Barry G. Becker, 2013 - 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.cave.model.CaveModel
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}

import javax.swing.*
import java.awt.*


/**
  * Interactively explores generating cave systems.
  * @author Barry Becker.
  */
object CaveExplorer {
  protected val INITIAL_TIME_STEP = 10.0
  protected val DEFAULT_STEPS_PER_FRAME = 10
}

class CaveExplorer() extends Simulator("Cave Explorer") {

  private var caveModel: CaveModel = _
  private var options: DynamicOptions = _
  private var handler: InteractionHandler = _
  commonInit()

  def getInteractionHandler: InteractionHandler = handler

  private def commonInit(): Unit = {
    caveModel = new CaveModel
    initCommonUI()
    handler = new InteractionHandler(caveModel, caveModel.getScale)
    this.addMouseListener(handler)
    this.addMouseMotionListener(handler)
  }

  override def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createResetButton)
    controls.add(createOptionsButton)
    controls
  }

  override protected def reset(): Unit = {
    setNumStepsPerFrame(CaveExplorer.DEFAULT_STEPS_PER_FRAME)
    // remove handlers to void memory leak
    this.removeMouseListener(handler)
    this.removeMouseMotionListener(handler)
    if (options != null) options.reset()
    commonInit()
  }

  override protected def createOptionsDialog = new SimulatorOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = CaveExplorer.INITIAL_TIME_STEP

  override def timeStep: Double = {
    if (!isPaused) {
      caveModel.setSize(this.getWidth, this.getHeight)
      caveModel.timeStep(tStep)
    }
    tStep
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    super.paint(g)
    Profiler.getInstance.startRenderingTime()
    g.drawImage(caveModel.getImage, 0, 0, null)
    Profiler.getInstance.stopRenderingTime()
  }

  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(caveModel, this)
    setPaused(false)
    options
  }
}
