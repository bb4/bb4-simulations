// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem

import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.lsystem.model.LSystemModel
import javax.swing.JPanel
import java.awt.Graphics


/**
  * Interactively explores LSystem generated trees based on a user supplied expression.
  * See http://en.wikipedia.org/wiki/L-system
  * Todo:
  *  - add ability to pan with mouse
  *  - add dropdown for preset expressions (like F-F+F+FF-F-F+F)
  *
  * @author Barry Becker.
  */
object LSystemExplorer {
  protected val INITIAL_TIME_STEP = 10.0
  protected val DEFAULT_STEPS_PER_FRAME = 10
}

class LSystemExplorer() extends Simulator("LSystem Tree Explorer") {

  private var algorithm: LSystemModel = _
  var handler: InteractionHandler = _
  private var options: DynamicOptions = _
  private var useFixedSize = false
  commonInit()

  /**
    * @param fixed if true then the render area does not resize automatically.
    */
  private[lsystem] def setUseFixedSize(fixed: Boolean): Unit = {
    useFixedSize = fixed
  }

  private def commonInit(): Unit = {
    algorithm = new LSystemModel
    handler = new InteractionHandler(algorithm)
    this.addMouseListener(handler)
    this.addMouseMotionListener(handler)
    initCommonUI()
    reset()
  }

  override protected def reset(): Unit = {
    algorithm.reset()
    setNumStepsPerFrame(LSystemExplorer.DEFAULT_STEPS_PER_FRAME)
    if (options != null) options.reset()
  }

  private[lsystem] def getUseFixedSize = useFixedSize
  override protected def createOptionsDialog = new OptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = LSystemExplorer.INITIAL_TIME_STEP
  override def setScale(scale: Double) {}
  override def getScale = 0.01

  override def timeStep: Double = {
    if (!isPaused) {
      if (!useFixedSize) algorithm.setSize(this.getWidth, this.getHeight)
      algorithm.timeStep(tStep)
    }
    tStep
  }

  override def paint(g: Graphics) {
    if (g == null) return
    super.paint(g)
    Profiler.getInstance.startRenderingTime()
    g.drawImage(algorithm.getImage, 0, 0, null)
    Profiler.getInstance.stopRenderingTime()
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(algorithm, this)
    setPaused(false)
    options
  }
}
