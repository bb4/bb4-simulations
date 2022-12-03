// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.common.Profiler
import com.barrybecker4.simulation.common.ui.{Simulator, SimulatorOptionsDialog}
import com.barrybecker4.simulation.voronoi.DynamicOptions
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiAlgorithm

import javax.swing.*
import java.awt.*


/**
  * @author Barry Becker.
  */
object VoronoiExplorer {
  protected val DEFAULT_STEPS_PER_FRAME = 1
}

class VoronoiExplorer() extends Simulator("Voronoi Explorer") {

  private var algorithm: VoronoiAlgorithm = _
  private var options: DynamicOptions = _
  private var useFixedSize = false
  commonInit()

  /** @param fixed if true then the render area does not resize automatically.*/
  def setUseFixedSize(fixed: Boolean): Unit = {
    useFixedSize = fixed
  }

  private def commonInit(): Unit = {
    algorithm = new VoronoiAlgorithm(this)
    initCommonUI()
    reset()
  }

  override protected def reset(): Unit = {
    algorithm.reset()
    setNumStepsPerFrame(VoronoiExplorer.DEFAULT_STEPS_PER_FRAME)
    if (options != null) options.reset()
  }

  def getUseFixedSize: Boolean = useFixedSize
  override protected def createOptionsDialog = new SimulatorOptionsDialog(frame, this)
  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01
  override def getInitialTimeStep = 1

  override def timeStep: Double = {
    if (!isPaused && this.getWidth > 0) {
      if (!useFixedSize) algorithm.setSize(this.getWidth, this.getHeight)
      algorithm.nextStep()
    }
    tStep
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    super.paint(g)
    Profiler.getInstance.startRenderingTime()
    g.drawImage(algorithm.getImage, 0, 0, null)
    Profiler.getInstance.stopRenderingTime()
  }

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(algorithm, this)
    options
  }
}
