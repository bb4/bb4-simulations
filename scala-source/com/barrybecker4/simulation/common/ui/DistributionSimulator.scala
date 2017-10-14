// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.ui.animation.AnimationFrame
import com.barrybecker4.ui.renderers.HistogramRenderer
import java.awt.Dimension
import java.awt.Graphics


/**
  * Simulates the the generation of a histogram based on
  * some stochastic process.
  * @author Barry Becker
  */
object DistributionSimulator {
  private val TIME_STEP = 1.0
  private val DEFAULT_STEPS_PER_FRAME = 100

  def runSimulation(simulator: DistributionSimulator): Unit = {
    simulator.setPaused(false)
    new AnimationFrame(simulator)
  }
}

abstract class DistributionSimulator(val title: String) extends Simulator(title) {

  protected var histogram: HistogramRenderer = _
  protected var data: Array[Int] = _
  commonInit()

  override protected def reset(): Unit = {
    initHistogram()
  }

  protected def initHistogram(): Unit

  private def commonInit(): Unit = {
    initCommonUI()
    setNumStepsPerFrame(DistributionSimulator.DEFAULT_STEPS_PER_FRAME)
    this.setPreferredSize(new Dimension(600, 500))
  }

  override protected def createOptionsDialog: SimulatorOptionsDialog
  override protected def getInitialTimeStep: Double = DistributionSimulator.TIME_STEP

  override def timeStep: Double = {
    if (!isPaused) histogram.increment(getXPositionToIncrement)
    tStep
  }

  /**
    * @return An x value to add to the histogram.
    *         The histogram itself will convert it to the correct x axis bin location.
    */
  protected def getXPositionToIncrement: Double

  override def paint(g: Graphics): Unit = {
    histogram.setSize(getWidth, getHeight)
    histogram.paint(g)
  }
}