// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT 
package com.barrybecker4.simulation.conway

import com.barrybecker4.simulation.conway.model.ConwayModel
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog
import javax.swing._
import java.awt._
import scala.compiletime.uninitialized

/**
  * Interactively explores Conway's game of life.
  * See https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
  * @author Barry Becker.
  */
class ConwayExplorer() extends Simulator("Conway's Game of Life Explorer") {
  private var conwayModel: ConwayModel = uninitialized
  private var options: Option[DynamicOptions] = None
  private var handler: InteractionHandler = uninitialized
  commonInit()

  private def commonInit(): Unit = {
    conwayModel = new ConwayModel
    initCommonUI()
    handler = new InteractionHandler(conwayModel, conwayModel.getScale)
    this.addMouseListener(handler)
    this.addMouseMotionListener(handler)
  }

  override protected def reset(): Unit = {
    setNumStepsPerFrame(1)
    // remove handlers to avoid memory leak
    this.removeMouseListener(handler)
    this.removeMouseMotionListener(handler)
    options.foreach(_.reset())
    commonInit()
  }

  private[conway] def getInteractionHandler = handler
  override protected def createOptionsDialog = new SimulatorOptionsDialog(frame, this)
  override protected def getInitialTimeStep = 1
  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01

  override def createTopControls: JPanel = {
    val controls = new JPanel
    controls.add(createResetButton)
    controls.add(createOptionsButton)
    controls
  }

  override def timeStep: Double = {
    if (!isPaused) {
      conwayModel.setSize(this.getWidth, this.getHeight)
      conwayModel.timeStep(tStep)
    }
    tStep
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    super.paint(g)
    Profiler.getInstance.startRenderingTime()
    g.drawImage(conwayModel.getImage, 0, 0, null)
    Profiler.getInstance.stopRenderingTime()
  }

  override def createDynamicControls: JPanel = {
    val panel = new DynamicOptions(conwayModel, this)
    options = Some(panel)
    setPaused(false)
    panel
  }
}
