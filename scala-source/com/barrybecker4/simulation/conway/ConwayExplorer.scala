// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.conway

import com.barrybecker4.simulation.conway.model.ConwayModel
import com.barrybecker4.simulation.common1.Profiler
import com.barrybecker4.simulation.common1.ui.Simulator
import javax.swing._
import java.awt._


/**
  * Interactively explores Conway's game of life.
  * See https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
  *
  * @author Barry Becker.
  */
class ConwayExplorer() extends Simulator("Conway's Game of Life Explorer") {
  commonInit()
  private var conwayModel: ConwayModel = _
  private var options: DynamicOptions = _
  private var handler: InteractionHandler = _

  private def commonInit() = {
    conwayModel = new ConwayModel
    initCommonUI()
    handler = new InteractionHandler(conwayModel, conwayModel.getScale)
    this.addMouseListener(handler)
    this.addMouseMotionListener(handler)
  }

  override protected def reset(): Unit = {
    setNumStepsPerFrame(1)
    // remove handlers to void memory leak
    this.removeMouseListener(handler)
    this.removeMouseMotionListener(handler)
    if (options != null) options.reset()
    commonInit()
  }

  private[conway] def getInteractionHandler = handler
  override protected def createOptionsDialog = new OptionsDialog(frame, this)
  override protected def getInitialTimeStep = 1
  override def setScale(scale: Double) {}
  override def getScale = 0.01

  override def timeStep: Double = {
    if (!isPaused) {
      conwayModel.setSize(this.getWidth, this.getHeight)
      conwayModel.timeStep(timeStep_)
    }
    timeStep_
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    super.paint(g)
    Profiler.getInstance.startRenderingTime()
    g.drawImage(conwayModel.getImage, 0, 0, null)
    Profiler.getInstance.stopRenderingTime()
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(conwayModel, this)
    setPaused(false)
    options
  }
}
