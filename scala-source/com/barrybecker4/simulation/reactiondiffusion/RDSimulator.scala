/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottController
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDRenderingOptions
import javax.swing._
import java.awt._


/**
  * Reaction diffusion simulator.
  * Based on work by Joakim Linde and modified by Barry Becker.
  */
object RDSimulator {
  val INITIAL_TIME_STEP = 1.0
  val DEFAULT_STEPS_PER_FRAME = 10
}

class RDSimulator() extends Simulator("Reaction Diffusion") {

  private var grayScott: GrayScottController = _
  private var viewer: RDViewer = _
  private var rdOptions: RDDynamicOptions = _
  private var handler: InteractionHandler = _
  commonInit()

  /**
    * @param fixed if true then the render area does not resize automatically.
    */
  def setUseFixedSize(fixed: Boolean) {
    viewer.setUseFixedSize(fixed)
  }

  def getUseFixedSize: Boolean = viewer.getUseFixedSize

  def setUseOffscreenRendering(use: Boolean) {
    viewer.setUseOffscreenRendering(use)
  }

  def getUseOffScreenRendering: Boolean = viewer.getUseOffScreenRendering

  override def setPaused(bPaused: Boolean) {
    super.setPaused(bPaused)
    if (isPaused) {
      RDProfiler.getInstance.print()
      RDProfiler.getInstance.resetAll()
    }
  }

  private def commonInit(): Unit = {
    initCommonUI()
    grayScott = new GrayScottController(1, 1)
    setNumStepsPerFrame(RDSimulator.DEFAULT_STEPS_PER_FRAME)
    viewer = new RDViewer(grayScott, this)
    handler = new InteractionHandler(grayScott.model, 1)
    this.addMouseListener(handler)
    this.addMouseMotionListener(handler)
  }

  def getInteractionHandler: InteractionHandler = handler

  override protected def reset() {
    grayScott.reset()
    rdOptions.reset()
  }

  override protected def createOptionsDialog = new RDOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = RDSimulator.INITIAL_TIME_STEP

  override def timeStep: Double = {
    if (!isPaused) {
      RDProfiler.getInstance.startCalculationTime()
      grayScott.timeStep(tStep)
      RDProfiler.getInstance.stopCalculationTime()
    }
    tStep
  }

  override def paint(g: Graphics) {
    super.paint(g)
    RDProfiler.getInstance.startRenderingTime()
    viewer.paint(g)
    RDProfiler.getInstance.stopRenderingTime()
  }

  override def setScale(scale: Double) {}
  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    rdOptions = new RDDynamicOptions(grayScott, this)
    rdOptions
  }

  def getColorMap: ColorMap = viewer.getColorMap
  def getRenderingOptions: RDRenderingOptions = viewer.getRenderingOptions
}
